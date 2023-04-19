package com.thoth.server.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.thoth.server.beans.IAuthenticationFacade;
import com.thoth.server.controller.dto.datasource.RestDatasourceParameters;
import com.thoth.server.model.domain.Client;
import com.thoth.server.model.domain.datasource.DatasourceProperties;
import com.thoth.server.model.domain.datasource.JdbcDatasourceProperties;
import com.thoth.server.model.domain.datasource.RestDatasourceProperties;
import com.thoth.server.model.repository.DatasourcePropertiesRepository;
import com.thoth.server.model.repository.RendererRepository;
import org.springframework.amqp.rabbit.connection.SimpleResourceHolder;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import java.time.Instant;
import java.util.*;

@Service
public class DataSourceService {

    private final ObjectMapper objectMapper;

    private final DatasourcePropertiesRepository datasourcePropertiesRepository;

    private final IAuthenticationFacade facade;
    private HashMap<String, DataSource> datasourceCache = new HashMap<>();

    public DataSourceService(ObjectMapper objectMapper, DatasourcePropertiesRepository datasourcePropertiesRepository, IAuthenticationFacade facade) {
        this.objectMapper = objectMapper;
        this.datasourcePropertiesRepository = datasourcePropertiesRepository;
        this.facade = facade;
    }


    public String[] checkJdbc(String url, String username, String password, String query, HashMap<String, Object> parameters) {
        var dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.url(url);
        dataSourceBuilder.username(username);
        dataSourceBuilder.password(password);
        var dataSource = dataSourceBuilder.build();

        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        var rs = jdbcTemplate.queryForRowSet(query, parameters);

        return rs.getMetaData().getColumnNames();
    }


    public JdbcDatasourceProperties createJdbc(String name, String url, String username, String password, String query, List<String> parameters, List<String> properties) {
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



    public DatasourceProperties update(String identifier, DatasourceProperties properties) {
        return update(findById(identifier).orElseThrow(), properties);
    }
    @PreAuthorize("@authenticationFacade.canWrite(#original)")
    private DatasourceProperties update(DatasourceProperties original, DatasourceProperties update) {
        update.setId(original.getId());
        return datasourcePropertiesRepository.save(update);
    }

    @PreAuthorize("@authenticationFacade.canWrite(#properties)")
    public void delete(DatasourceProperties properties) {
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

    @PreAuthorize("@authenticationFacade.canRead(#datasourceProperty) || hasRole('ROLE_TMP')")
    public HashMap<String, Object> fetchData(DatasourceProperties datasourceProperty, HashMap<String, Object> parameters) throws JsonProcessingException {
        if(datasourceProperty instanceof JdbcDatasourceProperties j){
            var dataSource = datasourceCache.get(j.getId());
            if(dataSource == null){
                var dataSourceBuilder = DataSourceBuilder.create();
                dataSourceBuilder.url(j.getUrl());
                dataSourceBuilder.username(j.getUsername());
                dataSourceBuilder.password(j.getPassword());
                dataSource = dataSourceBuilder.build();
                datasourceCache.put(j.getId(), dataSource);
            }

            for (Map.Entry<String, ?> e : parameters.entrySet()) {
                parameters.put(e.getKey(), isNumeric(e.getValue()) ? Float.parseFloat(e.getValue().toString()) : e.getValue());
            }

            NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
            var rs = jdbcTemplate.queryForRowSet(j.getQuery(), parameters);

            if(!rs.next()){
                return new HashMap<>();
            }
            var resp = new HashMap<String, Object>();
            for (String columnName : rs.getMetaData().getColumnNames()) {
                resp.put(columnName, rs.getObject(columnName));
            }
            return resp;
        }
        if(datasourceProperty instanceof RestDatasourceProperties j){
            var request  = new RestDatasourceParameters();
            request.setBody(j.getBody());
            request.setUrl(j.getUrl());
            request.setHeaders(j.getHeaders());
            request.setMethod(j.getMethod());
            request.setQueryParameters(j.getQueryParameters());
            request.setJsonQuery(j.getJsonQuery());
            var strBody = objectMapper.writeValueAsString(request);
            for (Map.Entry<String, Object> e : parameters.entrySet()) {
                strBody = strBody.replace("{{"+e.getKey()+"}}", e.getValue().toString());
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
            if(!root.getNodeType().equals(JsonNodeType.OBJECT))
                throw new IllegalArgumentException("Invalid json Object fetched");

            return getNodeFieldsMap(root, "");
        }
       return new HashMap<>();
    }


    public HashMap<String, Object> fetchData(String id, HashMap<String, Object> params) throws Exception {
        return fetchData(datasourcePropertiesRepository.findById(id).orElseThrow(), params);
    }

    public ArrayList<String> checkRest(RestDatasourceParameters request, HashMap<String, String> parameters) throws JsonProcessingException {
        var strBody = objectMapper.writeValueAsString(request);
        for (Map.Entry<String, String> e : parameters.entrySet()) {
            strBody = strBody.replace("{{"+e.getKey()+"}}", e.getValue());
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
        if(!root.getNodeType().equals(JsonNodeType.OBJECT))
            throw new IllegalArgumentException("Invalid json Object fetched");

        return getNodeFields(root, "");
    }

    private ArrayList<String> getNodeFields(JsonNode root, String s) {
        var fields = new ArrayList<String>();
        root.fields().forEachRemaining(e -> {
            if(e.getValue().getNodeType() == JsonNodeType.OBJECT){
                fields.addAll(getNodeFields(e.getValue(), e.getKey() + '/'));
            }
            else if(e.getValue().getNodeType() != JsonNodeType.ARRAY){
                fields.add(s + e.getKey());
            }
        });
        return fields;
    }

    private HashMap<String, Object> getNodeFieldsMap(JsonNode root, String s) {
        var fields = new HashMap<String, Object>();
        root.fields().forEachRemaining(e -> {
            if(e.getValue().getNodeType() == JsonNodeType.OBJECT){
                fields.putAll(getNodeFieldsMap(e.getValue(), e.getKey() + '/'));
            }
            else if(e.getValue().getNodeType() != JsonNodeType.ARRAY){
                fields.put(s + e.getKey(), e.getValue().asText());
            }
        });
        return fields;
    }

    public RestDatasourceProperties createRest(String name, String url, String method, Map<String, String> queryParameters, Map<String, String> headers, String jsonQuery, Map<String, Object> body, List<String> parameters, List<String> properties) {
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
