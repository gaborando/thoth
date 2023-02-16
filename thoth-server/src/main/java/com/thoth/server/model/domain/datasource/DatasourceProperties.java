package com.thoth.server.model.domain.datasource;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;

@Entity
@Getter
@Setter
public abstract class DatasourceProperties {
    @Id
    private String id;

    private String name;

    private String type;

    @ElementCollection
    private List<String> parameters;

    @ElementCollection
    private List<String> properties;
}
