package com.thoth.server.configuration.security.token;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class DefaultAuthenticationToken extends ThothAuthenticationToken {

    public DefaultAuthenticationToken() {
        super("default-user", "default-org");
    }

    @Override
    public Object getCredentials() {
        return null;
    }
}
