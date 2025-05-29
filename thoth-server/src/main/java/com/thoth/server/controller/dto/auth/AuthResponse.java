package com.thoth.server.controller.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response object for authentication requests
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private String refreshToken;

    /**
     * Constructor with only access token
     * 
     * @param token The access token
     */
    public AuthResponse(String token) {
        this.token = token;
    }
}
