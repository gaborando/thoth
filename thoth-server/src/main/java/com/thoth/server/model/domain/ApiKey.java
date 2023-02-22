package com.thoth.server.model.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
public class ApiKey {

    @Id
    private String id;

    @NotBlank
    @Size(max = 256)
    private String name;


    @Column(updatable = false)
    private String apiKey;

    private String userSID;

    private String organizationSID;

    private Instant expiry;

    private Instant createdAt;
}
