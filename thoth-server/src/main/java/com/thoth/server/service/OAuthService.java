package com.thoth.server.service;

import com.thoth.server.model.domain.security.User;
import org.springframework.stereotype.Service;

/**
 * Service for handling OAuth authentication flows
 */
public interface OAuthService {

    /**
     * Get the authorization URL for the specified OAuth provider
     * 
     * @param provider The OAuth provider (e.g., "google", "github")
     * @return The authorization URL to redirect the user to
     */
    String getAuthorizationUrl(String provider);

    /**
     * Handle the OAuth callback and authenticate the user
     * 
     * @param provider The OAuth provider
     * @param code The authorization code returned by the provider
     * @return Authentication result with access and refresh tokens
     */
    UserService.AuthenticationResult handleCallback(String provider, String code);

    /**
     * Check if the specified provider is supported
     * 
     * @param provider The OAuth provider to check
     * @return true if the provider is supported, false otherwise
     */
    boolean isProviderSupported(String provider);

    /**
     * Get a list of supported OAuth providers
     * 
     * @return Array of supported provider names
     */
    String[] getSupportedProviders();
}
