package com.thoth.server.configuration.security.token;

import com.thoth.server.configuration.security.SecuredTimestamp;
import com.thoth.server.model.domain.ApiKey;
import org.springframework.security.core.GrantedAuthority;

import java.time.Instant;
import java.util.List;

public class TempAuthenticationToken extends ThothAuthenticationToken{
    private final SecuredTimestamp securedTimestamp;

    public TempAuthenticationToken(SecuredTimestamp securedTimestamp) {
        super("temp-user", "temp-org", List.of((GrantedAuthority) () -> "ROLE_TMP"));
        this.securedTimestamp = securedTimestamp;
    }

    @Override
    public SecuredTimestamp getCredentials() {
        return securedTimestamp;
    }
}
