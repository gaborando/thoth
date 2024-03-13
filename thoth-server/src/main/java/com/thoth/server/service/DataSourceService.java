package com.thoth.server.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.mysema.commons.lang.Assert;
import com.nimbusds.jose.shaded.gson.Gson;
import com.thoth.server.beans.IAuthenticationFacade;
import com.thoth.server.controller.dto.datasource.RestDatasourceParameters;
import com.thoth.server.model.domain.datasource.DatasourceProperties;
import com.thoth.server.model.domain.datasource.JdbcDatasourceProperties;
import com.thoth.server.model.domain.datasource.Property;
import com.thoth.server.model.domain.datasource.RestDatasourceProperties;
import com.thoth.server.model.repository.DatasourcePropertiesRepository;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DataSourceService {

    private final ObjectMapper objectMapper;

    private final DatasourcePropertiesRepository datasourcePropertiesRepository;

    private final IAuthenticationFacade facade;
    private final HashMap<String, DataSource> datasourceCache = new HashMap<>();

    private final SecretService secretService;

    public DataSourceService(ObjectMapper objectMapper, DatasourcePropertiesRepository datasourcePropertiesRepository, IAuthenticationFacade facade, SecretService secretService) {
        this.objectMapper = objectMapper;
        this.datasourcePropertiesRepository = datasourcePropertiesRepository;
        this.facade = facade;
        this.secretService = secretService;
    }


    public Set<Property> checkJdbc(String url, String username, String password, String query, HashMap<String, Object> parameters) {
        var dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.url(compileSecrets(url, true));
        dataSourceBuilder.username(compileSecrets(username, true));
        dataSourceBuilder.password(compileSecrets(password, true));
        query = compileSecrets(query, true);
        var dataSource = dataSourceBuilder.build();
        for (String k : parameters.keySet()) {
            query = query.replace("{{" + k + "}}", ':' + k);
        }
        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        var rs = jdbcTemplate.queryForRowSet(query, parameters);

        var map = new HashSet<Property>();
        if (!rs.next()) {
            throw new IllegalStateException("Query with 0 results");
        }
        for (String columnName : rs.getMetaData().getColumnNames()) {
            map.add(new Property(columnName, String.valueOf(rs.getObject(columnName))));
        }
        return map;
    }


    public JdbcDatasourceProperties createJdbc(String name, String url, String username, String password, String query,
                                               Set<String> parameters, Set<Property> properties) {
        var dsp = new JdbcDatasourceProperties();
        dsp.setId("dsp_jdbc_" + UUID.randomUUID());
        dsp.setUrl(url);
        dsp.setUsername(username);
        dsp.setPassword(password);
        dsp.setQuery(query);
        dsp.setParameters(parameters);
        dsp.setProperties(properties);
        dsp.setName(name);
        dsp.setType("jdbc");
        dsp.setCreatedAt(Instant.now());
        facade.fillSecuredResource(dsp);
        return datasourcePropertiesRepository.save(dsp);
    }


    public Page<DatasourceProperties> search(Specification<DatasourceProperties> specification, PageRequest pageable) {
        return datasourcePropertiesRepository.findAll(facade.securedSpecification(specification, DatasourceProperties.class), pageable);
    }

    @PostAuthorize("@authenticationFacade.canRead(returnObject)")
    public Optional<DatasourceProperties> findById(String identifier) {
        return datasourcePropertiesRepository.findById(identifier);
    }




    @PreAuthorize("@authenticationFacade.canWrite(#original)")
    public DatasourceProperties update(DatasourceProperties original, DatasourceProperties update) {
        update.setId(original.getId());
        datasourceCache.remove(original.getId());
        return datasourcePropertiesRepository.save(update);
    }

    @PreAuthorize("@authenticationFacade.canWrite(#properties)")
    public void delete(DatasourceProperties properties) {
        Assert.isTrue(properties.getUsages().isEmpty(), "This Datasource is currently used in " + properties.getUsages().size() + " renderers.");
        datasourcePropertiesRepository.delete(properties);
    }

    public static boolean isNumeric(Object strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum.toString());
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    private String compileSecrets(String value, boolean checkAuth){
        Pattern pattern = Pattern.compile("\\$\\{([^}]+)\\}");
        Matcher matcher = pattern.matcher(value);

        while (matcher.find()) {
            String variable = matcher.group(1);
            value = value.replace("${" + variable + "}", secretService.getValue(variable, checkAuth));
        }
        return value;
    }

    @PreAuthorize("@authenticationFacade.canRead(#datasourceProperty) || hasRole('ROLE_TMP')")
    public HashMap<String, Object> fetchData(DatasourceProperties datasourceProperty, HashMap<String, Object> parameters) throws JsonProcessingException {
        if (datasourceProperty instanceof JdbcDatasourceProperties j) {
            var dataSource = datasourceCache.get(j.getId());

            if (dataSource == null) {
                var dataSourceBuilder = DataSourceBuilder.create();
                dataSourceBuilder.url(compileSecrets(j.getUrl(), false));
                dataSourceBuilder.username(compileSecrets(j.getUsername(), false));
                dataSourceBuilder.password(compileSecrets(j.getPassword(), false));
                dataSource = dataSourceBuilder.build();
                datasourceCache.put(j.getId(), dataSource);
            }

            var localParameters = new HashMap<String, Object>();

            for (Map.Entry<String, ?> e : parameters.entrySet()) {
                localParameters.put(e.getKey(), isNumeric(e.getValue()) ? toNumeric(e.getValue().toString()) : e.getValue());
            }

            var query = compileSecrets(j.getQuery(), false);

            for (String k : j.getParameters()) {
                query = query.replace("{{" + k + "}}", ':' + k);
            }
            NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
            var rs = jdbcTemplate.queryForRowSet(query, localParameters);

            if (!rs.next()) {
                return new HashMap<>();
            }
            var resp = new HashMap<String, Object>();
            for (String columnName : rs.getMetaData().getColumnNames()) {
                resp.put(columnName, rs.getObject(columnName));
            }
            return resp;
        }
        if (datasourceProperty instanceof RestDatasourceProperties j) {
            var request = new RestDatasourceParameters();
            request.setBody(j.getBody());
            request.setUrl(j.getUrl());
            request.setHeaders(j.getHeaders());
            request.setMethod(j.getMethod());
            request.setQueryParameters(j.getQueryParameters());
            request.setJsonQuery(j.getJsonQuery());
            var strBody = compileSecrets(objectMapper.writeValueAsString(request), false);

            for (Map.Entry<String, Object> e : parameters.entrySet()) {
                strBody = strBody.replace("{{" + e.getKey() + "}}", e.getValue().toString());
            }
            if(strBody.contains("{{") && strBody.contains("}}")){
                throw new IllegalStateException("Missing parameter for datasource");
            }
            request = objectMapper.readValue(strBody, RestDatasourceParameters.class);
            var restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            request.getHeaders().forEach(headers::set);
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
            var resp = restTemplate.exchange(
                    request.getUrl(),
                    HttpMethod.valueOf(request.getMethod()),
                    requestEntity,
                    String.class,
                    request.getQueryParameters()
            );

            var root = objectMapper.readTree(resp.getBody()).at(request.getJsonQuery());
            if (!root.getNodeType().equals(JsonNodeType.OBJECT))
                throw new IllegalArgumentException("Invalid json Object fetched");

            return getNodeFieldsMap(root, "");
        }
        return new HashMap<>();
    }

    private Object toNumeric(String string) {
        var num = Float.parseFloat(string);
        if (BigDecimal.valueOf(num).divideAndRemainder(BigDecimal.ONE)[1].equals(BigDecimal.ZERO)) {
            return (int) num;
        }
        return num;
    }


    public static String addParameterToURL(String urlString, String paramName, String paramValue) throws URISyntaxException {
        URI uri = new URI(urlString);

        // Check if the URL already has query parameters
        String query = uri.getQuery();
        if (query == null) {
            query = paramName + "=" + paramValue;
        } else {
            query += "&" + paramName + "=" + paramValue;
        }

        // Construct the new URI with updated query parameters
        URI newUri = new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), query, uri.getFragment());

        return newUri.toString();
    }


    public Set<Property> checkRest(RestDatasourceParameters request, HashMap<String, String> parameters) throws JsonProcessingException, MalformedURLException, URISyntaxException {
        var strBody = compileSecrets(objectMapper.writeValueAsString(request), true);

        for (Map.Entry<String, String> e : parameters.entrySet()) {
            strBody = strBody.replace("{{" + e.getKey() + "}}", e.getValue());
        }
        request = objectMapper.readValue(strBody, RestDatasourceParameters.class);
        var restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        request.getHeaders().forEach(headers::set);
        var url = request.getUrl();
        for (Map.Entry<String, String> e : request.getQueryParameters().entrySet()) {
            url = addParameterToURL(url, e.getKey(), e.getValue());
        }
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        var resp = restTemplate.exchange(
                url,
                HttpMethod.valueOf(request.getMethod()),
                requestEntity,
                String.class,
                request.getQueryParameters()
        );

        var root = objectMapper.readTree(resp.getBody()).at(request.getJsonQuery());
        if (!root.getNodeType().equals(JsonNodeType.OBJECT))
            throw new IllegalArgumentException("Invalid json Object fetched");

        return getNodeFields(root, "");
    }

    private Set<Property> getNodeFields(JsonNode root, String s) {
        var fields = new HashSet<Property>();
        root.fields().forEachRemaining(e -> {
            if (e.getValue().getNodeType() == JsonNodeType.OBJECT) {
                fields.addAll(getNodeFields(e.getValue(), s + e.getKey() + '/'));
            } else {
                fields.add(new Property(s + e.getKey(), e.getValue().textValue()));
            }
        });
        return fields;
    }

    private HashMap<String, Object> getNodeFieldsMap(JsonNode root, String s) {
        var fields = new HashMap<String, Object>();
        root.fields().forEachRemaining(e -> {
            if (e.getValue().getNodeType() == JsonNodeType.OBJECT) {
                fields.putAll(getNodeFieldsMap(e.getValue(), s + e.getKey() + '/'));
            } else if (e.getValue().getNodeType() != JsonNodeType.ARRAY) {
                fields.put(s + e.getKey(), e.getValue().asText());
            } else {
                fields.put(s + e.getKey(), e.getValue());
            }
        });
        return fields;
    }

    public RestDatasourceProperties createRest(String name, String url, String
            method, Map<String, String> queryParameters,
                                               Map<String, String> headers,
                                               String jsonQuery,
                                               Map<String, Object> body,
                                               Set<String> parameters,
                                               Set<Property> properties) {
        var dsp = new RestDatasourceProperties();
        dsp.setId("dsp_rest_" + UUID.randomUUID());
        dsp.setUrl(url);
        dsp.setMethod(method);
        dsp.setQueryParameters(queryParameters);
        dsp.setHeaders(headers);
        dsp.setJsonQuery(jsonQuery);
        dsp.setBody(body);
        dsp.setParameters(parameters);
        dsp.setProperties(properties);
        dsp.setName(name);
        dsp.setType("rest");
        dsp.setCreatedAt(Instant.now());
        facade.fillSecuredResource(dsp);
        return datasourcePropertiesRepository.save(dsp);
    }
}
