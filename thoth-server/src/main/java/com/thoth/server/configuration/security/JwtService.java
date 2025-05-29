package com.thoth.server.configuration.security;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.thoth.server.model.domain.security.RefreshToken;
import com.thoth.server.model.domain.security.User;
import com.thoth.server.model.repository.RefreshTokenRepository;
import com.thoth.server.model.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JwtService {

    private final String jwkUrl;
    private final String issuer;
    private final String jwtSecret;
    private final String userSidClaim;
    private final String organizationSidClaim;
    private final long tokenExpirationMs;
    private final long refreshTokenExpirationMs;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public JwtService(
            @Value("${thoth.oauth.jwk.url}") String jwkUrl,
            @Value("${thoth.oauth.issuer.url}") String issuer,
            @Value("${thoth.jwt.secret:thothDefaultSecretKey}") String jwtSecret,
            @Value("${thoth.oauth.claim.sid.user}") String userSidClaim,
            @Value("${thoth.oauth.claim.sid.organization}") String organizationSidClaim,
            @Value("${thoth.jwt.expiration:86400000}") long tokenExpirationMs,
            @Value("${thoth.jwt.refresh.expiration:604800000}") long refreshTokenExpirationMs,
            RefreshTokenRepository refreshTokenRepository,
            UserRepository userRepository) {
        this.jwkUrl = jwkUrl;
        this.issuer = issuer;
        this.jwtSecret = jwtSecret;
        this.userSidClaim = userSidClaim;
        this.organizationSidClaim = organizationSidClaim;
        this.tokenExpirationMs = tokenExpirationMs;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
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

    /**
     * Generate a JWT token for the given username and roles
     * 
     * @param username The username to include in the token
     * @param roles The roles to include in the token
     * @return The generated JWT token as a string
     */
    public String generateToken(String username, Set<String> roles) {
        try {
            // Create JWT claims set
            Date now = new Date();
            Date expiration = new Date(now.getTime() + tokenExpirationMs);

            JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder()
                    .subject(username)
                    .issuer(issuer)
                    .issueTime(now)
                    .expirationTime(expiration)
                    .jwtID(UUID.randomUUID().toString());

            // Add user SID claim
            claimsBuilder.claim(userSidClaim, username);

            // Add organization SID claim if available (using first role as organization)
            if (roles != null && !roles.isEmpty()) {
                String firstRole = roles.iterator().next();
                claimsBuilder.claim(organizationSidClaim, firstRole);
            }

            // Add roles as a claim
            if (roles != null && !roles.isEmpty()) {
                claimsBuilder.claim("roles", roles.stream().collect(Collectors.toList()));
            }

            // Create signed JWT
            JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.HS256).type(JOSEObjectType.JWT).build();
            SignedJWT signedJWT = new SignedJWT(header, claimsBuilder.build());

            // Sign the JWT
            JWSSigner signer = new MACSigner(jwtSecret.getBytes());
            signedJWT.sign(signer);

            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("Error generating JWT token", e);
        }
    }

    /**
     * Create a refresh token for a user
     * 
     * @param username The username to create a refresh token for
     * @return The created refresh token
     */
    @Transactional
    public RefreshToken createRefreshToken(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        // Create a new refresh token
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenExpirationMs));

        // Save and return the new token
        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * Verify if a refresh token is valid
     * 
     * @param token The refresh token to verify
     * @return The refresh token if valid
     */
    public RefreshToken verifyRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        if (!refreshToken.isValid()) {
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("Refresh token is not valid");
        }

        return refreshToken;
    }

    /**
     * Refresh an access token using a refresh token
     * 
     * @param refreshToken The refresh token to use
     * @return A new access token
     */
    @Transactional
    public String refreshAccessToken(String refreshToken) {
        RefreshToken token = verifyRefreshToken(refreshToken);

        // Mark the token as used
        token.markAsUsed();
        refreshTokenRepository.save(token);

        // Create a new access token
        User user = token.getUser();
        return generateToken(user.getUsername(), user.getRoles());
    }

    /**
     * Revoke all refresh tokens for a user
     * 
     * @param username The username to revoke tokens for
     */
    @Transactional
    public void revokeAllUserTokens(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        refreshTokenRepository.revokeAllUserTokens(user);
    }

    /**
     * Delete all expired refresh tokens
     */
    @Transactional
    public void deleteExpiredTokens() {
        refreshTokenRepository.deleteAllExpiredTokens(Instant.now());
    }
}
