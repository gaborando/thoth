package com.thoth.server.controller.view;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
public class UserView extends SecuredResourceView {
    private String username;
    private String email;
    private Instant createdAt;
    private Instant lastLoginAt;
    private boolean enabled;
}
