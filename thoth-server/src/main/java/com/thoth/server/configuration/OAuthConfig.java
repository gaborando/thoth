package com.thoth.server.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration properties for OAuth providers
 */
@Configuration
@ConfigurationProperties(prefix = "thoth.oauth")
public class OAuthConfig {
    
    private boolean enabled;
    private String jwkUrl;
    private String issuerUrl;
    private String userSidClaim;
    private String organizationSidClaim;
    private Map<String, ProviderConfig> providers = new HashMap<>();
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public String getJwkUrl() {
        return jwkUrl;
    }
    
    public void setJwkUrl(String jwkUrl) {
        this.jwkUrl = jwkUrl;
    }
    
    public String getIssuerUrl() {
        return issuerUrl;
    }
    
    public void setIssuerUrl(String issuerUrl) {
        this.issuerUrl = issuerUrl;
    }
    
    public String getUserSidClaim() {
        return userSidClaim;
    }
    
    public void setUserSidClaim(String userSidClaim) {
        this.userSidClaim = userSidClaim;
    }
    
    public String getOrganizationSidClaim() {
        return organizationSidClaim;
    }
    
    public void setOrganizationSidClaim(String organizationSidClaim) {
        this.organizationSidClaim = organizationSidClaim;
    }
    
    public Map<String, ProviderConfig> getProviders() {
        return providers;
    }
    
    public void setProviders(Map<String, ProviderConfig> providers) {
        this.providers = providers;
    }
    
    /**
     * Configuration for an OAuth provider
     */
    public static class ProviderConfig {
        private String clientId;
        private String clientSecret;
        private String authorizationUri;
        private String tokenUri;
        private String userInfoUri;
        private String redirectUri;
        private String scope;
        
        public String getClientId() {
            return clientId;
        }
        
        public void setClientId(String clientId) {
            this.clientId = clientId;
        }
        
        public String getClientSecret() {
            return clientSecret;
        }
        
        public void setClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
        }
        
        public String getAuthorizationUri() {
            return authorizationUri;
        }
        
        public void setAuthorizationUri(String authorizationUri) {
            this.authorizationUri = authorizationUri;
        }
        
        public String getTokenUri() {
            return tokenUri;
        }
        
        public void setTokenUri(String tokenUri) {
            this.tokenUri = tokenUri;
        }
        
        public String getUserInfoUri() {
            return userInfoUri;
        }
        
        public void setUserInfoUri(String userInfoUri) {
            this.userInfoUri = userInfoUri;
        }
        
        public String getRedirectUri() {
            return redirectUri;
        }
        
        public void setRedirectUri(String redirectUri) {
            this.redirectUri = redirectUri;
        }
        
        public String getScope() {
            return scope;
        }
        
        public void setScope(String scope) {
            this.scope = scope;
        }
    }
}