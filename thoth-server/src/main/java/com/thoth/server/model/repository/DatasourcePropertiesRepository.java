package com.thoth.server.model.repository;

import com.thoth.server.model.domain.datasource.DatasourceProperties;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;


public interface DatasourcePropertiesRepository extends JpaRepository<DatasourceProperties, String>, PagingAndSortingRepository<DatasourceProperties, String>, JpaSpecificationExecutor<DatasourceProperties> {
}