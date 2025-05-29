package com.thoth.server.controller.view;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * View class for Organization entity
 */
@Getter
@Setter
public class OrganizationView extends SecuredResourceView {
    private Long id;
    private String name;
    private String description;
    private Instant createdAt;
}