package com.thoth.server.model.domain.datasource;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;

@Entity
@Getter
@Setter
public abstract class DatasourceProperties {
    @Id
    @NotBlank
    @Size(min = 3, max = 256)
    private String id;

    @NotBlank
    @Size(min = 3, max = 256)
    private String name;

    @NotBlank
    @Size(min = 3, max = 256)
    private String type;

    @ElementCollection
    private List<@NotBlank @Size(min = 1, max = 256) String> parameters;

    @ElementCollection
    private List<@NotBlank @Size(min = 1, max = 256) String> properties;
}
