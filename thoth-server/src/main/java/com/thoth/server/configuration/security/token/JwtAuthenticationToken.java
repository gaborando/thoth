package com.thoth.server.configuration.security.token;

import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

public class JwtAuthenticationToken extends ThothAuthenticationToken {
    private final JWTClaimsSet claims;

    /**
     * Creates a token with the supplied array of authorities.
     */
    public JwtAuthenticationToken(
            JWTClaimsSet claims,
            String userSidClaim,
            String organizationSidClaim) {
        super(claims.getClaim(userSidClaim).toString(), extractOrg(claims, organizationSidClaim));
        this.claims = claims;

    }

    private static String extractOrg(JWTClaimsSet claims, String organizationSidClaim) {
        var org = claims.getClaim(organizationSidClaim);
        if (org instanceof Iterable<?> i) {
            return i.iterator().next().toString();
        }
        return org.toString();
    }

    @Override
    public Object getCredentials() {
        return claims;
    }
}
