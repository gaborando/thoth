package com.thoth.server.controller.dto.datasource;

import com.thoth.server.model.domain.datasource.DatasourceProperties;
import com.thoth.server.model.domain.datasource.Property;

import java.util.Map;
import java.util.Set;

public class DatasourcePropertyListItem {
    private String id;
    private String name;
    private String type;
    private Set<String> parameters;
    private Set<Property> properties;

    public DatasourcePropertyListItem(DatasourceProperties item) {
        this.id = item.getId();
        this.name = item.getName();
        this.type = item.getType();
        this.properties = item.getProperties();
        this.parameters = item.getParameters();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Set<String> getParameters() {
        return parameters;
    }

    public void setParameters(Set<String> parameters) {
        this.parameters = parameters;
    }

    public Set<Property> getProperties() {
        return properties;
    }

    public DatasourcePropertyListItem setProperties(Set<Property> properties) {
        this.properties = properties;
        return this;
    }
}
