package com.thoth.server.model.domain.security;

import com.thoth.server.controller.view.UserView;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User extends SecuredResource {

    @NotBlank
    @Size(min = 3, max = 50)
    @Column(unique = true)
    @Id
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


    // OAuth2 related fields
    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private HashMap<String, String> oauthProviderIdentityMap;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<UserGroupAssociation> groups;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }

    public UserView toView(User user) {
        UserView view = new UserView();
        view.setUsername(username);
        view.setEmail(email);
        view.setCreatedAt(createdAt);
        view.setLastLoginAt(lastLoginAt);
        view.setEnabled(enabled);

        setView(view, user);
        return view;
    }
}
