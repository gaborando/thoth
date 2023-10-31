package com.thoth.server.controller.dto.datasource;

import lombok.Data;

import java.util.HashMap;

@Data
public class RestDatasourceParametersCheckRequest extends RestDatasourceParameters {
    private HashMap<String, String> parameters;
}
