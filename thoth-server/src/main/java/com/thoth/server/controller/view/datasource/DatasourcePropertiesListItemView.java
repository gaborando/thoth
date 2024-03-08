package com.thoth.server.controller.view.datasource;

import com.thoth.server.model.domain.Permission;
import com.thoth.server.model.domain.datasource.Property;
import lombok.Data;

import java.util.Set;

@Data
public class DatasourcePropertiesListItemView {
    private String id;
    private String name;
    private String type;
    private Set<String> parameters;
    private Set<Property> properties;
    private Permission permission;
}
