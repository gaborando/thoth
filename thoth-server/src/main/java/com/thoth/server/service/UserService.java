package com.thoth.server.service;

import com.thoth.server.beans.IAuthenticationFacade;
import com.thoth.server.configuration.security.JwtService;
import com.thoth.server.configuration.security.token.JwtAuthenticationToken;
import com.thoth.server.controller.dto.auth.AuthResponse;
import com.thoth.server.model.domain.security.Permission;
import com.thoth.server.model.domain.security.RefreshToken;
import com.thoth.server.model.domain.security.ResourcePermission;
import com.thoth.server.model.domain.security.User;
import com.thoth.server.model.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository repository;
    private final IAuthenticationFacade authenticationFacade;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(UserRepository repository, 
                      IAuthenticationFacade authenticationFacade, 
                      PasswordEncoder passwordEncoder,
                      JwtService jwtService) {
        this.repository = repository;
        this.authenticationFacade = authenticationFacade;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public User registerUser(String username, String email, String password, Set<String> roles) {
        if (repository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }
        if (repository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setCreatedAt(Instant.now());
        user.setEnabled(true);
        user.setRoles(roles != null ? roles : new HashSet<>(Set.of("ROLE_USER")));
        user.setAllowedUserList(new ArrayList<>());
        user.setAllowedOrganizationList(new ArrayList<>());

        // Set the creator to the user itself (for the first user) or the current user
        String creatorSid = authenticationFacade.getUserSID();
        if (creatorSid == null || repository.count() == 0) {
            // First user or no authentication - set as self-created
            // For the first user, we need to set the user as its own creator
            // This creates a circular reference, but it's saved after the user is created
            user.setCreatedBy(user);
        } else {
            // Find the creator user by username
            User creator = repository.findByUsername(creatorSid)
                    .orElseThrow(() -> new RuntimeException("Creator user not found: " + creatorSid));
            user.setCreatedBy(creator);

            // Add permission for the creator if it's not the same user
            if (!username.equals(creatorSid)) {
                ResourcePermission permission = new ResourcePermission();
                permission.setSid(creatorSid);
                permission.setPermission(Permission.W);
                user.getAllowedUserList().add(permission);
            }
        }

        return repository.save(user);
    }

    public User registerOAuthUser(String email, String oauthProvider, String oauthId, String name) {
        Optional<User> existingUser = repository.findByEmail(email);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            // Update OAuth info if this is a new OAuth connection
            if (user.getOauthProvider() == null || !user.getOauthProvider().equals(oauthProvider)) {
                user.setOauthProvider(oauthProvider);
                user.setOauthId(oauthId);
                return repository.save(user);
            }
            return user;
        }

        // Create new user
        User user = new User();
        user.setUsername(email); // Use email as username for OAuth users
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(java.util.UUID.randomUUID().toString())); // Random password
        user.setCreatedAt(Instant.now());
        user.setEnabled(true);
        user.setRoles(new HashSet<>(Set.of("ROLE_USER")));
        user.setOauthProvider(oauthProvider);
        user.setOauthId(oauthId);
        user.setAllowedUserList(new ArrayList<>());
        user.setAllowedOrganizationList(new ArrayList<>());

        // Set the creator to the user itself
        // This creates a circular reference, but it's saved after the user is created
        user.setCreatedBy(user);

        return repository.save(user);
    }

    /**
     * Authenticate a user with username and password
     * 
     * @param username The username
     * @param password The password
     * @return Authentication response with access and refresh tokens
     */
    public AuthenticationResult authenticateUser(String username, String password) {
        User user = repository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isEnabled()) {
            throw new RuntimeException("User is disabled");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        // Update last login time
        user.setLastLoginAt(Instant.now());
        repository.save(user);

        // Generate JWT token
        String accessToken = jwtService.generateToken(user.getUsername(), user.getRoles());

        // Generate refresh token
        RefreshToken refreshToken = jwtService.createRefreshToken(user.getUsername());

        return new AuthenticationResult(accessToken, refreshToken.getToken());
    }

    /**
     * Authenticate a user with OAuth provider and ID
     * 
     * @param oauthProvider The OAuth provider
     * @param oauthId The OAuth ID
     * @return Authentication response with access and refresh tokens
     */
    public AuthenticationResult authenticateOAuthUser(String oauthProvider, String oauthId) {
        User user = repository.findByOauthProviderAndOauthId(oauthProvider, oauthId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isEnabled()) {
            throw new RuntimeException("User is disabled");
        }

        // Update last login time
        user.setLastLoginAt(Instant.now());
        repository.save(user);

        // Generate JWT token
        String accessToken = jwtService.generateToken(user.getUsername(), user.getRoles());

        // Generate refresh token
        RefreshToken refreshToken = jwtService.createRefreshToken(user.getUsername());

        return new AuthenticationResult(accessToken, refreshToken.getToken());
    }

    /**
     * Refresh an access token using a refresh token
     * 
     * @param refreshToken The refresh token
     * @return Authentication response with new access and refresh tokens
     */
    public AuthenticationResult refreshToken(String refreshToken) {
        // Refresh the access token
        String accessToken = jwtService.refreshAccessToken(refreshToken);

        // Return the new tokens
        return new AuthenticationResult(accessToken, refreshToken);
    }

    /**
     * Class to hold authentication result with access and refresh tokens
     */
    public static class AuthenticationResult {
        private final String accessToken;
        private final String refreshToken;

        public AuthenticationResult(String accessToken, String refreshToken) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public String getRefreshToken() {
            return refreshToken;
        }
    }

    public Page<User> search(Specification<User> where, PageRequest of) {
        return repository.findAll(authenticationFacade.securedSpecification(where, User.class), of);
    }

    @PostAuthorize("@authenticationFacade.canRead(returnObject)")
    public Optional<User> findById(Long id) {
        return repository.findById(id);
    }

    @PostAuthorize("@authenticationFacade.canRead(returnObject)")
    public Optional<User> findByUsername(String username) {
        return repository.findByUsername(username);
    }

    @PreAuthorize("@authenticationFacade.canWrite(#original)")
    public User update(User original, User update) {
        // Don't update username or email to existing values
        if (!original.getUsername().equals(update.getUsername()) && 
            repository.existsByUsername(update.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (!original.getEmail().equals(update.getEmail()) && 
            repository.existsByEmail(update.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        original.setUsername(update.getUsername());
        original.setEmail(update.getEmail());
        original.setEnabled(update.isEnabled());
        original.setRoles(update.getRoles());
        original.setAllowedUserList(update.getAllowedUserList());
        original.setAllowedOrganizationList(update.getAllowedOrganizationList());

        // Only update password if it's provided
        if (update.getPassword() != null && !update.getPassword().isEmpty()) {
            original.setPassword(passwordEncoder.encode(update.getPassword()));
        }

        return repository.save(original);
    }

    @PreAuthorize("@authenticationFacade.canWrite(#user)")
    public void delete(User user) {
        repository.delete(user);
    }
}
