package com.thoth.server.controller.view;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * View class for Organization entity
 */
@Getter
@Setter
public class GroupView extends SecuredResourceView {
    private String name;
    private String description;
    private Instant createdAt;
}