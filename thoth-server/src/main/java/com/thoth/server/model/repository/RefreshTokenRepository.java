package com.thoth.server.model.repository;

import com.thoth.server.model.domain.security.RefreshToken;
import com.thoth.server.model.domain.security.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Repository for RefreshToken entity
 */
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    
    /**
     * Find a token by its value
     * 
     * @param token the token value
     * @return the token if found
     */
    Optional<RefreshToken> findByToken(String token);
    
    /**
     * Find all tokens for a user
     * 
     * @param user the user
     * @return list of tokens
     */
    List<RefreshToken> findAllByUser(User user);
    
    /**
     * Find all valid tokens for a user
     * 
     * @param user the user
     * @param now current time
     * @return list of valid tokens
     */
    List<RefreshToken> findAllByUserAndExpiryDateAfterAndRevokedFalseAndUsedAtIsNull(User user, Instant now);
    
    /**
     * Delete all expired tokens
     * 
     * @param now current time
     * @return number of deleted tokens
     */
    @Modifying
    @Query("DELETE FROM RefreshToken t WHERE t.expiryDate < :now")
    int deleteAllExpiredTokens(@Param("now") Instant now);
    
    /**
     * Revoke all tokens for a user
     * 
     * @param user the user
     * @return number of revoked tokens
     */
    @Modifying
    @Query("UPDATE RefreshToken t SET t.revoked = true WHERE t.user = :user AND t.revoked = false")
    int revokeAllUserTokens(@Param("user") User user);
}