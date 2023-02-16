package com.thoth.server.model.domain;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Entity
@Getter
@Setter
public class Client {

    @Id
    private String identifier;
    private String name;

    @ElementCollection
    private List<String> printServices;

}
