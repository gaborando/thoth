package com.thoth.server.model.repository;

import com.thoth.server.model.domain.security.OAuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

/**
 * Repository for OAuthProvider entity
 */
public interface OAuthProviderRepository extends JpaRepository<OAuthProvider, Long>, 
        PagingAndSortingRepository<OAuthProvider, Long>, 
        JpaSpecificationExecutor<OAuthProvider> {
    
    /**
     * Find a provider by its name
     * 
     * @param name the provider name
     * @return the provider if found
     */
    Optional<OAuthProvider> findByName(String name);
    
    /**
     * Check if a provider with the given name exists
     * 
     * @param name the provider name
     * @return true if exists, false otherwise
     */
    boolean existsByName(String name);
}