package com.thoth.server.model.domain.security;

import com.thoth.server.controller.view.OrganizationView;
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
@Table(name = "organizations")
public class Organization extends SecuredResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3, max = 100)
    @Column(unique = true)
    private String name;

    @Size(max = 500)
    private String description;

    @Column(updatable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User> users = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }

    public OrganizationView toView(String uSid, String oSid) {
        OrganizationView view = new OrganizationView();
        view.setId(id);
        view.setName(name);
        view.setDescription(description);
        view.setCreatedAt(createdAt);
        setView(view, uSid, oSid);
        return view;
    }
}