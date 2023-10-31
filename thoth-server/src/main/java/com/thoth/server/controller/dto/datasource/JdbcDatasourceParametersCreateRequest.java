package com.thoth.server.controller.dto.datasource;

import com.thoth.server.model.domain.datasource.Property;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public class JdbcDatasourceParametersCreateRequest {

    private String name;
    private String url;
    private String username;
    private String password;
    private String query;
    private Set<String> parameters;
    private Set<Property> properties;
}
