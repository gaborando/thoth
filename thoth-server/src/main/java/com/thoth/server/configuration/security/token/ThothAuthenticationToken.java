package com.thoth.server.configuration.security.token;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public abstract class ThothAuthenticationToken extends AbstractAuthenticationToken {

    private final String userSID;
    private final String organizationSID;

    public ThothAuthenticationToken(String userSID, String organizationSID) {
        super(List.of((GrantedAuthority) () -> "ROLE_USER"));
        this.userSID = userSID;
        this.organizationSID = organizationSID;
        this.setAuthenticated(true);
    }

    public ThothAuthenticationToken(String userSID, String organizationSID, List<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.userSID = userSID;
        this.organizationSID = organizationSID;
        this.setAuthenticated(true);
    }

    @Override
    public String getPrincipal() {
        return getUserSID();
    }

    public String getUserSID() {
        return userSID;
    }

    public String getOrganizationSID() {
        return organizationSID;
    }
}
