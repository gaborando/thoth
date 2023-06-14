package com.thoth.server.controller.dto.datasource;

import com.thoth.server.model.domain.datasource.DatasourceProperties;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.FetchType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public class DatasourcePropertyListItem {
    private String id;
    private String name;
    private String type;
    private List<String> parameters;
    private List<String> properties;
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

    public List<String> getParameters() {
        return parameters;
    }

    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }

    public List<String> getProperties() {
        return properties;
    }

    public void setProperties(List<String> properties) {
        this.properties = properties;
    }
}
