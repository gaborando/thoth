package com.thoth.server.controller;

import com.thoth.server.beans.AuthenticationFacade;
import com.thoth.server.controller.dto.auth.AuthResponse;
import com.thoth.server.controller.dto.auth.LoginRequest;
import com.thoth.server.controller.dto.auth.RegisterRequest;
import com.thoth.server.controller.view.UserView;
import com.thoth.server.model.domain.security.User;
import com.thoth.server.service.OAuthService;
import com.thoth.server.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final OAuthService oAuthService;

    public AuthController(UserService userService, OAuthService oAuthService) {
        this.userService = userService;
        this.oAuthService = oAuthService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserView> register(@Valid @RequestBody RegisterRequest request, AuthenticationFacade facade) {
        User user = userService.registerUser(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                new HashSet<>(request.getRoles())
        );

        return ResponseEntity.ok(user.toView(facade.getUserSID(), facade.getOrganizationSID()));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        UserService.AuthenticationResult result = userService.authenticateUser(request.getUsername(), request.getPassword());

        return ResponseEntity.ok(new AuthResponse(result.getAccessToken(), result.getRefreshToken()));
    }

    @GetMapping("/oauth/{provider}")
    public ResponseEntity<?> getOAuthUrl(@PathVariable String provider) {
        try {
            if (!oAuthService.isProviderSupported(provider)) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Unsupported OAuth provider",
                    "supportedProviders", oAuthService.getSupportedProviders()
                ));
            }

            String authUrl = oAuthService.getAuthorizationUrl(provider);
            return ResponseEntity.ok(Map.of("authorizationUrl", authUrl));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/oauth/providers")
    public ResponseEntity<?> getSupportedProviders() {
        return ResponseEntity.ok(Map.of(
            "providers", oAuthService.getSupportedProviders()
        ));
    }

    @GetMapping("/oauth/callback/{provider}")
    public ResponseEntity<?> oauthCallback(
            @PathVariable String provider,
            @RequestParam String code) {
        try {
            if (!oAuthService.isProviderSupported(provider)) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Unsupported OAuth provider",
                    "supportedProviders", oAuthService.getSupportedProviders()
                ));
            }

            UserService.AuthenticationResult result = oAuthService.handleCallback(provider, code);
            return ResponseEntity.ok(new AuthResponse(result.getAccessToken(), result.getRefreshToken()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Refresh an access token using a refresh token
     * 
     * @param refreshToken The refresh token
     * @return A new access token
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestParam String refreshToken) {
        try {
            UserService.AuthenticationResult result = userService.refreshToken(refreshToken);
            return ResponseEntity.ok(new AuthResponse(result.getAccessToken(), result.getRefreshToken()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new AuthResponse());
        }
    }
}
