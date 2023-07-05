package com.thoth.server.model.domain;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;

@Entity
@Getter
@Setter
public class Template extends SecuredResource {
    @Id
    @Column(updatable = false)
    @NotBlank
    @Size(min = 3, max = 256)
    private String id;

    @NotBlank
    @Size(min = 3, max = 256)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String svg;

    @Column(columnDefinition = "TEXT")
    private String img;

    @Column(columnDefinition = "TEXT")
    private String xml;

    @Column(updatable = false)
    private Instant createdAt;

    @ElementCollection
    private Set<@NotBlank @Size(min = 2, max = 256) String> markers;

    @NotBlank
    @Size(min = 3, max = 256)
    private String folder;

}
