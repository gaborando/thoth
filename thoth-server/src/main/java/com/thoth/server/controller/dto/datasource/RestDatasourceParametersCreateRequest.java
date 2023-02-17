package com.thoth.server.controller.dto.datasource;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;

@Getter
@Setter
public class RestDatasourceParametersCreateRequest extends RestDatasourceParameters {

    private String name;
    private List<String> parameters;

    private List<String> properties;
}
