package com.thoth.server.model.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@MappedSuperclass
public abstract class SecuredResource {
    @Column(updatable = false)
    private String createdBy;
    @ElementCollection
    private List<ResourcePermission> allowedUserList;
    @ElementCollection
    private List<ResourcePermission> allowedOrganizationList;
}
