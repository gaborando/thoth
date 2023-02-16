package com.thoth.server.controller.dto.datasource;

import lombok.Data;

import java.util.HashMap;

@Data
public class RestDatasourceParametersCheckRequest {
    private RestDatasourceParameters request;
    private HashMap<String, String> parameters;
}
