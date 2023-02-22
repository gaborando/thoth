package com.thoth.server.configuration.security.token;

import com.thoth.server.model.domain.ApiKey;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public class ApiKetAuthenticationToken extends ThothAuthenticationToken{
    private final ApiKey apiKey;

    public ApiKetAuthenticationToken(ApiKey apiKey) {
        super(apiKey.getUserSID(), apiKey.getOrganizationSID(), List.of((GrantedAuthority) () -> "ROLE_USER"));
        this.apiKey = apiKey;
    }

    @Override
    public ApiKey getCredentials() {
        return apiKey;
    }
}
