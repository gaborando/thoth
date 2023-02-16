package com.thoth.server.model.repository;

import com.thoth.server.model.domain.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ClientRepository extends JpaRepository<Client, String>, PagingAndSortingRepository<Client, String>, JpaSpecificationExecutor<Client> {
}