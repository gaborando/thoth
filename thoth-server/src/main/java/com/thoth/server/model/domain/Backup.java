package com.thoth.server.model.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
public class Backup {

    @Id
    @Column(updatable = false)
    @NotBlank
    @Size(min = 3, max = 256)
    private String identifier;

    @Column(updatable = false)
    @NotBlank
    @Size(min = 3, max = 256)
    private String resourceId;

    @Enumerated(EnumType.STRING)
    private ResourceType resourceType;

    @Column(columnDefinition = "LONGTEXT")
    private String data;

    @Column(updatable = false)
    private Instant timestamp;
}
