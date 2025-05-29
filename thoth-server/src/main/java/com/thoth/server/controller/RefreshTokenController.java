package com.thoth.server.controller;

import com.thoth.server.model.domain.security.RefreshToken;
import com.thoth.server.model.domain.security.User;
import com.thoth.server.model.repository.RefreshTokenRepository;
import com.thoth.server.model.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller for managing refresh tokens
 */
@RestController
@RequestMapping("/refresh-tokens")
@Secured("ROLE_ADMIN") // Only admins can manage refresh tokens
public class RefreshTokenController {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public RefreshTokenController(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    /**
     * Get all refresh tokens for a user
     * 
     * @param userId User ID
     * @return List of refresh tokens
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RefreshToken>> getTokensByUser(@PathVariable Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        List<RefreshToken> tokens = refreshTokenRepository.findAllByUser(user.get());
        return ResponseEntity.ok(tokens);
    }

    /**
     * Get all valid refresh tokens for a user
     * 
     * @param userId User ID
     * @return List of valid refresh tokens
     */
    @GetMapping("/user/{userId}/valid")
    public ResponseEntity<List<RefreshToken>> getValidTokensByUser(@PathVariable Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        List<RefreshToken> tokens = refreshTokenRepository.findAllByUserAndExpiryDateAfterAndRevokedFalseAndUsedAtIsNull(
                user.get(), Instant.now());
        return ResponseEntity.ok(tokens);
    }

    /**
     * Revoke a refresh token
     * 
     * @param tokenId Token ID
     * @return Success message
     */
    @PostMapping("/{tokenId}/revoke")
    public ResponseEntity<Map<String, String>> revokeToken(@PathVariable Long tokenId) {
        Optional<RefreshToken> token = refreshTokenRepository.findById(tokenId);
        if (token.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        RefreshToken refreshToken = token.get();
        refreshToken.revoke();
        refreshTokenRepository.save(refreshToken);
        
        return ResponseEntity.ok(Map.of("message", "Token revoked successfully"));
    }

    /**
     * Revoke all tokens for a user
     * 
     * @param userId User ID
     * @return Success message
     */
    @PostMapping("/user/{userId}/revoke-all")
    public ResponseEntity<Map<String, String>> revokeAllTokensForUser(@PathVariable Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        int count = refreshTokenRepository.revokeAllUserTokens(user.get());
        return ResponseEntity.ok(Map.of(
                "message", "Revoked " + count + " tokens for user",
                "count", String.valueOf(count)
        ));
    }

    /**
     * Delete expired tokens
     * 
     * @return Success message
     */
    @DeleteMapping("/expired")
    public ResponseEntity<Map<String, String>> deleteExpiredTokens() {
        int count = refreshTokenRepository.deleteAllExpiredTokens(Instant.now());
        return ResponseEntity.ok(Map.of(
                "message", "Deleted " + count + " expired tokens",
                "count", String.valueOf(count)
        ));
    }

    /**
     * Delete a token
     * 
     * @param tokenId Token ID
     * @return Success message
     */
    @DeleteMapping("/{tokenId}")
    public ResponseEntity<Void> deleteToken(@PathVariable Long tokenId) {
        Optional<RefreshToken> token = refreshTokenRepository.findById(tokenId);
        if (token.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        refreshTokenRepository.delete(token.get());
        return ResponseEntity.noContent().build();
    }
}