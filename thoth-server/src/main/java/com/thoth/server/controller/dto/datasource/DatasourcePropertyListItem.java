package com.thoth.server.controller.dto.datasource;

import com.thoth.server.model.domain.datasource.DatasourceProperties;

public class DatasourcePropertyListItem {
    private String id;
    private String name;
    private String type;
    public DatasourcePropertyListItem(DatasourceProperties item) {
        this.id = item.getId();
        this.name = item.getName();
        this.type = item.getType();
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
}
