package com.thoth.server.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoth.server.controller.dto.datasource.RestDatasourceParameters;
import com.thoth.server.model.domain.datasource.DatasourceProperties;
import com.thoth.server.model.domain.datasource.JdbcDatasourceProperties;
import com.thoth.server.model.repository.DatasourcePropertiesRepository;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class DataSourceService {

    private final ObjectMapper objectMapper;

    private final DatasourcePropertiesRepository datasourcePropertiesRepository;

    public DataSourceService(ObjectMapper objectMapper, DatasourcePropertiesRepository datasourcePropertiesRepository) {
        this.objectMapper = objectMapper;
        this.datasourcePropertiesRepository = datasourcePropertiesRepository;
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
        return datasourcePropertiesRepository.save(dsp);
    }

    public Page<DatasourceProperties> search(Specification<DatasourceProperties> specification, PageRequest pageable) {
        return datasourcePropertiesRepository.findAll(specification, pageable);
    }

    public Optional<DatasourceProperties> findById(String identifier) {
        return datasourcePropertiesRepository.findById(identifier);
    }

    public DatasourceProperties update(DatasourceProperties properties) {
        return datasourcePropertiesRepository.save(properties);
    }

    public void delete(String identifier) {
        datasourcePropertiesRepository.deleteById(identifier);
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

    public HashMap<String, Object> fetchData(DatasourceProperties datasourceProperty, HashMap<String, Object> parameters) {
        if(datasourceProperty instanceof JdbcDatasourceProperties j){
            var dataSourceBuilder = DataSourceBuilder.create();
            dataSourceBuilder.url(j.getUrl());
            dataSourceBuilder.username(j.getUsername());
            dataSourceBuilder.password(j.getPassword());
            var dataSource = dataSourceBuilder.build();

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
       return new HashMap<>();
    }


    public HashMap<String, Object> fetchData(String id, HashMap<String, Object> params) {
        return fetchData(datasourcePropertiesRepository.findById(id).orElseThrow(), params);
    }

    public String[] checkRest(RestDatasourceParameters request, HashMap<String, String> parameters) throws JsonProcessingException {
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
                HashMap.class,
                request.getQueryParameters()
        );
        var props = new ArrayList<String>();
        for (Object o : resp.getBody().keySet()) {
            props.add(o.toString());
        }
        return props.toArray(String[]::new);
    }
}
