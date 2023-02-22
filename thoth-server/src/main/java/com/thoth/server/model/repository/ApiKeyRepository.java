package com.thoth.server.model.repository;

import com.thoth.server.model.domain.ApiKey;
import com.thoth.server.model.domain.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface ApiKeyRepository extends JpaRepository<ApiKey, String>, PagingAndSortingRepository<ApiKey, String>, JpaSpecificationExecutor<ApiKey> {
    Optional<ApiKey> findApiKeyByApiKey(String key);
}
