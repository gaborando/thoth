package com.thot.server.controller.dto.datasource;

import lombok.Data;

import java.util.HashMap;
@Data
public class JdbcDatasourceParametersCheckRequest {
    private String url;
    private String username;
    private String password;
    private String query;

    private HashMap<String, Object> parameters;
}
