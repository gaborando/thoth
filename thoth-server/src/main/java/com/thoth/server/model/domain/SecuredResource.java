package com.thoth.server.model.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@MappedSuperclass
public abstract class SecuredResource {
    @Column(updatable = false)
    private String createdBy;
    @ElementCollection
    private Set<String> allowedUserList;
    @ElementCollection
    private Set<String> allowedOrganizationList;
}
