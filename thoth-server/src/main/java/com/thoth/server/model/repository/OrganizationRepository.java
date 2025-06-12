package com.thoth.server.model.repository;

import com.thoth.server.model.domain.security.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

/**
 * Repository for Organization entity
 */
public interface OrganizationRepository extends JpaRepository<Group, Long>,
        PagingAndSortingRepository<Group, Long>,
        JpaSpecificationExecutor<Group> {
    
    /**
     * Find an organization by its name
     * 
     * @param name the organization name
     * @return the organization if found
     */
    Optional<Group> findByName(String name);
    
    /**
     * Check if an organization with the given name exists
     * 
     * @param name the organization name
     * @return true if exists, false otherwise
     */
    boolean existsByName(String name);
}