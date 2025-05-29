package com.thoth.server.model.domain.security;

import com.thoth.server.controller.view.OAuthProviderView;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * Entity representing an OAuth provider configuration
 */
@Entity
@Getter
@Setter
@Table(name = "oauth_providers")
public class OAuthProvider extends SecuredResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3, max = 50)
    @Column(unique = true)
    private String name;

    @NotBlank
    @Size(max = 255)
    private String clientId;

    @NotBlank
    @Size(max = 255)
    private String clientSecret;

    @NotBlank
    @Size(max = 255)
    private String authorizationUri;

    @NotBlank
    @Size(max = 255)
    private String tokenUri;

    @NotBlank
    @Size(max = 255)
    private String userInfoUri;

    @NotBlank
    @Size(max = 255)
    private String redirectUri;

    @NotBlank
    @Size(max = 255)
    private String scope;

    @Column(updatable = false)
    private Instant createdAt;

    @Column
    private boolean enabled = true;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }

    public OAuthProviderView toView(String uSid, String oSid) {
        OAuthProviderView view = new OAuthProviderView();
        view.setId(id);
        view.setName(name);
        view.setClientId(clientId);
        // Don't expose client secret in the view
        view.setAuthorizationUri(authorizationUri);
        view.setTokenUri(tokenUri);
        view.setUserInfoUri(userInfoUri);
        view.setRedirectUri(redirectUri);
        view.setScope(scope);
        view.setCreatedAt(createdAt);
        view.setEnabled(enabled);
        setView(view, uSid, oSid);
        return view;
    }
}