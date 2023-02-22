package com.thoth.server.model.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
public class ApiKey {

    @Id
    private String id;

    private String apiKey;

    private String userSID;

    private String organizationSID;

    private Instant expiry;

    private Instant createdAt;
}
