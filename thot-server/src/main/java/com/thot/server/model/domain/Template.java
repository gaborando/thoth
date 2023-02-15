package com.thot.server.model.domain;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
public class Template {
    @Id
    private String id;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String svg;

    @Column(columnDefinition = "TEXT")
    private String img;

    @Column(columnDefinition = "TEXT")
    private String xml;

    @ElementCollection
    private Set<String> markers;

}
