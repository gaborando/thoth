package com.thoth.server.controller.dto.datasource;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class RestDatasourceParameters {
    private String url;
    private String method;
    private Map<String, String> queryParameters;
    private Map<String, String> headers;
    private Map<String, Object> body;
    private String jsonQuery;
}
