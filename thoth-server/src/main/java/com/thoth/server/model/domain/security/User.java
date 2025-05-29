package com.thoth.server.model.domain.security;

import com.thoth.server.controller.view.UserView;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User extends SecuredResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3, max = 50)
    @Column(unique = true)
    private String username;

    @NotBlank
    @Size(max = 120)
    private String password;

    @NotBlank
    @Size(max = 50)
    @Email
    @Column(unique = true)
    private String email;

    @Column(updatable = false)
    private Instant createdAt;

    @Column
    private Instant lastLoginAt;

    @Column
    private boolean enabled = true;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<String> roles = new HashSet<>();

    // OAuth2 related fields
    @Column
    private String oauthProvider;

    @Column
    private String oauthId;

    // Organization reference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }

    public UserView toView(String uSid, String oSid) {
        UserView view = new UserView();
        view.setId(id);
        view.setUsername(username);
        view.setEmail(email);
        view.setCreatedAt(createdAt);
        view.setLastLoginAt(lastLoginAt);
        view.setEnabled(enabled);
        view.setRoles(roles);
        view.setOauthProvider(oauthProvider);

        // Set organization information if available
        if (organization != null) {
            view.setOrganizationId(organization.getId());
            view.setOrganizationName(organization.getName());
        }

        setView(view, uSid, oSid);
        return view;
    }
}
