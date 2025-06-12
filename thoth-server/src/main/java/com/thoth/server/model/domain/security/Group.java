package com.thoth.server.model.domain.security;

import com.thoth.server.controller.view.GroupView;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing an organization in the system.
 * Organizations can have multiple users and resources.
 */
@Entity
@Getter
@Setter
public class Group extends SecuredResource {

    @NotBlank
    @Size(min = 3, max = 100)
    @Column(unique = true)
    @Id
    private String identifier;

    @Size(max = 500)
    private String description;

    @Column(updatable = false)
    private Instant createdAt;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserGroupAssociation> users = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }

    public GroupView toView(User user) {
        GroupView view = new GroupView();
        view.setName(identifier);
        view.setDescription(description);
        view.setCreatedAt(createdAt);
        setView(view, user);
        return view;
    }
}