package com.thoth.server.configuration.security.token;

import com.thoth.server.model.domain.ApiKey;

public class ApiKetAuthenticationToken extends ThothAuthenticationToken{
    private final ApiKey apiKey;

    public ApiKetAuthenticationToken(ApiKey apiKey) {
        super(apiKey.getUserSID(), apiKey.getOrganizationSID());
        this.apiKey = apiKey;
    }

    @Override
    public ApiKey getCredentials() {
        return apiKey;
    }
}
