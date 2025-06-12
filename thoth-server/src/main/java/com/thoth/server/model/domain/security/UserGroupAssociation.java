package com.thoth.server.model.domain.security;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class UserGroupAssociation {

    @Id
    private String identifier;

    @ManyToOne
    private User user;

    @ManyToOne
    private Group group;

    private boolean admin;

}
