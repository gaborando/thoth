package com.thot.server.controller.dto.datasource;

import lombok.Data;

import java.util.HashMap;
import java.util.List;

@Data
public class JdbcDatasourceParametersCreateRequest {

    private String name;
    private String url;
    private String username;
    private String password;
    private String query;

    private List<String> parameters;

    private List<String> properties;
}
