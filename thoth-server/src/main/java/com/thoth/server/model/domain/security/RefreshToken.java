package com.thoth.server.model.domain.security;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

/**
 * Entity representing a refresh token for JWT authentication
 */
@Entity
@Getter
@Setter
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Instant expiryDate;

    @Column(nullable = false)
    private Instant createdAt;

    @Column
    private Instant usedAt;

    @Column
    private boolean revoked = false;

    /**
     * Check if the token is expired
     * 
     * @return true if expired, false otherwise
     */
    public boolean isExpired() {
        return expiryDate.isBefore(Instant.now());
    }

    /**
     * Check if the token is valid (not expired and not revoked)
     * 
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return !isExpired() && !revoked && usedAt == null;
    }

    /**
     * Mark the token as used
     */
    public void markAsUsed() {
        this.usedAt = Instant.now();
    }

    /**
     * Revoke the token
     */
    public void revoke() {
        this.revoked = true;
    }

    /**
     * Generate a new token value
     */
    @PrePersist
    public void prePersist() {
        if (token == null) {
            token = UUID.randomUUID().toString();
        }
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}