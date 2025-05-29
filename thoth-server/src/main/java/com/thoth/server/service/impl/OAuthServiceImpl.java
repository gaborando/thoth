package com.thoth.server.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoth.server.configuration.OAuthConfig;
import com.thoth.server.model.domain.security.OAuthProvider;
import com.thoth.server.model.repository.OAuthProviderRepository;
import com.thoth.server.service.OAuthService;
import com.thoth.server.service.UserService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

@Service
public class OAuthServiceImpl implements OAuthService {

    private final OAuthConfig oAuthConfig; // Keep for backward compatibility during transition
    private final OAuthProviderRepository providerRepository;
    private final UserService userService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public OAuthServiceImpl(OAuthConfig oAuthConfig, 
                           OAuthProviderRepository providerRepository,
                           UserService userService) {
        this.oAuthConfig = oAuthConfig;
        this.providerRepository = providerRepository;
        this.userService = userService;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public String getAuthorizationUrl(String provider) {
        if (!isProviderSupported(provider)) {
            throw new IllegalArgumentException("Unsupported OAuth provider: " + provider);
        }

        // Try to get provider from database first
        OAuthProvider dbProvider = providerRepository.findByName(provider)
                .orElseThrow(() -> new IllegalArgumentException("Provider not found in database: " + provider));

        if (!dbProvider.isEnabled()) {
            throw new IllegalArgumentException("OAuth provider is disabled: " + provider);
        }

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(dbProvider.getAuthorizationUri())
                .queryParam("client_id", dbProvider.getClientId())
                .queryParam("redirect_uri", dbProvider.getRedirectUri())
                .queryParam("response_type", "code")
                .queryParam("scope", dbProvider.getScope());

        return builder.toUriString();
    }

    @Override
    public UserService.AuthenticationResult handleCallback(String provider, String code) {
        if (!isProviderSupported(provider)) {
            throw new IllegalArgumentException("Unsupported OAuth provider: " + provider);
        }

        // Get provider from database
        OAuthProvider dbProvider = providerRepository.findByName(provider)
                .orElseThrow(() -> new IllegalArgumentException("Provider not found in database: " + provider));

        if (!dbProvider.isEnabled()) {
            throw new IllegalArgumentException("OAuth provider is disabled: " + provider);
        }

        // Exchange code for access token
        String accessToken = getAccessToken(provider, code, dbProvider);

        // Get user info using the access token
        Map<String, String> userInfo = getUserInfo(provider, accessToken, dbProvider);

        // Register or authenticate the user
        String email = userInfo.get("email");
        String oauthId = userInfo.get("id");
        String name = userInfo.get("name");

        // Register or authenticate the user
        return userService.authenticateOAuthUser(provider, oauthId);
    }

    private String getAccessToken(String provider, String code, OAuthProvider dbProvider) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", dbProvider.getClientId());
        body.add("client_secret", dbProvider.getClientSecret());
        body.add("code", code);
        body.add("redirect_uri", dbProvider.getRedirectUri());
        body.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

        String response = restTemplate.exchange(
                dbProvider.getTokenUri(),
                HttpMethod.POST,
                entity,
                String.class
        ).getBody();

        try {
            JsonNode root = objectMapper.readTree(response);
            return root.path("access_token").asText();
        } catch (IOException e) {
            throw new RuntimeException("Error parsing OAuth token response", e);
        }
    }

    private Map<String, String> getUserInfo(String provider, String accessToken, OAuthProvider dbProvider) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        String response = restTemplate.exchange(
                dbProvider.getUserInfoUri(),
                HttpMethod.GET,
                entity,
                String.class
        ).getBody();

        try {
            JsonNode root = objectMapper.readTree(response);

            // Extract user info based on provider
            String id, email, name;

            switch (provider) {
                case "google":
                    id = root.path("sub").asText();
                    email = root.path("email").asText();
                    name = root.path("name").asText();
                    break;
                case "github":
                    id = root.path("id").asText();
                    email = root.path("email").asText();
                    name = root.path("name").asText();
                    break;
                default:
                    id = root.path("id").asText();
                    email = root.path("email").asText();
                    name = root.path("name").asText();
            }

            // Register the user if they don't exist
            userService.registerOAuthUser(email, provider, id, name);

            return Map.of(
                    "id", id,
                    "email", email,
                    "name", name
            );
        } catch (IOException e) {
            throw new RuntimeException("Error parsing OAuth user info response", e);
        }
    }

    @Override
    public boolean isProviderSupported(String provider) {
        // First check if provider exists in database
        return providerRepository.existsByName(provider);
    }

    @Override
    public String[] getSupportedProviders() {
        // Get all enabled providers from database
        return providerRepository.findAll().stream()
                .filter(OAuthProvider::isEnabled)
                .map(OAuthProvider::getName)
                .toArray(String[]::new);
    }
}
