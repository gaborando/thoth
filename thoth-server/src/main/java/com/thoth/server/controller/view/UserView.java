package com.thoth.server.controller.view;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
public class UserView extends SecuredResourceView {
    private Long id;
    private String username;
    private String email;
    private Instant createdAt;
    private Instant lastLoginAt;
    private boolean enabled;
    private Set<String> roles;
    private String oauthProvider;
    private Long organizationId;
    private String organizationName;
}
