package com.thoth.server.configuration.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashSet;

@Service
public class JwtService {

    private final String jwkUrl;
    private final String issuer;

    public JwtService(
            @Value("${thoth.oauth.jwk.url}") String jwkUrl,
            @Value("${thoth.oauth.issuer.url}") String issuer) {
        this.jwkUrl = jwkUrl;
        this.issuer = issuer;
    }

    public JWTClaimsSet verifyAndGetClaims(String jwt) throws MalformedURLException, BadJOSEException, ParseException, JOSEException {
        ConfigurableJWTProcessor<SecurityContext> jwtProcessor =
                new DefaultJWTProcessor<>();
        JWKSource<SecurityContext> keySource =
                new RemoteJWKSet<>(new URL(jwkUrl));
        JWSAlgorithm expectedJWSAlg = JWSAlgorithm.RS256;
        JWSKeySelector<SecurityContext> keySelector =
                new JWSVerificationKeySelector<>(expectedJWSAlg, keySource);
        jwtProcessor.setJWSKeySelector(keySelector);
        jwtProcessor.setJWTClaimsSetVerifier(new DefaultJWTClaimsVerifier<>(
                new JWTClaimsSet.Builder().issuer(issuer).build(),
                new HashSet<>(Arrays.asList("sub", "iat", "exp", "jti", "iss"))));
        return jwtProcessor.process(jwt, null);
    }
}
