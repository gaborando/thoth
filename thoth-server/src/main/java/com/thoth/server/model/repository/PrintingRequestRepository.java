package com.thoth.server.model.repository;

import com.thoth.server.model.domain.Client;
import com.thoth.server.model.domain.PrintingRequest;
import com.thoth.server.model.domain.PrintingRequestStatus;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface PrintingRequestRepository  extends JpaRepository<PrintingRequest, String>, PagingAndSortingRepository<PrintingRequest, String>, JpaSpecificationExecutor<PrintingRequest> {

    List<PrintingRequest> findAllByClientAndStatus(Client client, PrintingRequestStatus status, PageRequest createdAt);

    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "false"))
    @Query("SELECT e FROM PrintingRequest e WHERE e.identifier = :identifier")
    Optional<PrintingRequest> findByIdUncached(String identifier);
    @Query(value = "SELECT e.status FROM printing_request e WHERE e.identifier = :identifier", nativeQuery = true)
    PrintingRequestStatus getStatus(String identifier);

    @Query(value = "SELECT e.error_message FROM printing_request e WHERE e.identifier = :identifier", nativeQuery = true)
    String getError(String identifier);
}
