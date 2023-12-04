package com.thoth.server.model.repository;

import com.thoth.server.model.domain.Backup;
import com.thoth.server.model.domain.BackupListItem;
import com.thoth.server.model.domain.ResourceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BackupRepository extends JpaRepository<Backup, String> {

    @Query("select b.identifier as identifier, b.resourceId as resourceId, b.timestamp as timestamp from Backup b where b.resourceType = ?1 and b.resourceId = ?2 order by b.timestamp desc ")
    List<BackupListItem> findAllByResource(ResourceType resourceType, String resourceId);
}