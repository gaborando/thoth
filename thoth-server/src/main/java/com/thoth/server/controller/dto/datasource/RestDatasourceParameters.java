package com.thoth.server.controller.dto.datasource;

import lombok.Data;

import java.util.HashMap;

@Data
public class RestDatasourceParameters {
    private String url;
    private String method;
    private HashMap<String, String> queryParameters;
    private HashMap<String, String> headers;
    private HashMap<String, Object> body;
    private HashMap<String, Object> parameters;
}
