package com.thot.server.model.domain;

import com.thot.server.controller.dto.renderer.Association;
import com.thot.server.model.domain.datasource.DatasourceProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter
public class Renderer {

    @Id
    private String id;

    private String name;

    @ManyToOne
    private Template template;

    @ManyToMany
    @Column(insertable = false, updatable = false)
    private List<DatasourceProperties> datasourceProperties;

    @ElementCollection
    private Map<String, Association> associationMap;


}
