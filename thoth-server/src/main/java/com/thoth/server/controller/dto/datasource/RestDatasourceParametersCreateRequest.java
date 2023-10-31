package com.thoth.server.controller.dto.datasource;

import com.thoth.server.model.domain.datasource.Property;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
public class RestDatasourceParametersCreateRequest extends RestDatasourceParameters {

    private String name;
    private Set<String> parameters;
    private Set<Property> properties;
}
