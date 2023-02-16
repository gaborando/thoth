package com.thoth.server.model.domain;

import com.thoth.server.controller.dto.renderer.Association;
import com.thoth.server.model.domain.datasource.DatasourceProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter
public class Renderer {

    @Id
    @NotBlank
    @Size(min = 3, max = 256)
    private String id;

    @NotBlank
    @Size(min = 3, max = 256)
    private String name;

    @ManyToOne
    @NotNull
    private Template template;

    @ManyToMany
    @NotEmpty
    @Column(insertable = false, updatable = false)
    private List<DatasourceProperties> datasourceProperties;

    @ElementCollection
    private Map<@NotBlank @Size(min = 3, max = 256) String, @NotNull Association> associationMap;


}
