package com.thoth.server.controller.view;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * View class for OAuthProvider entity
 */
@Getter
@Setter
public class OAuthProviderView extends SecuredResourceView {
    private Long id;
    private String name;
    private String clientId;
    // Client secret is not included in the view for security reasons
    private String authorizationUri;
    private String tokenUri;
    private String userInfoUri;
    private String redirectUri;
    private String scope;
    private Instant createdAt;
    private boolean enabled;
}