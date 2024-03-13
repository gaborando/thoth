package com.thoth.server.model.repository;

import com.thoth.server.model.domain.ApiKey;
import com.thoth.server.model.domain.Secret;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface SecretRepository extends JpaRepository<Secret, String>, PagingAndSortingRepository<Secret, String>, JpaSpecificationExecutor<Secret> {
}
