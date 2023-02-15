package com.thot.server.model.repository;

import com.thot.server.model.domain.Renderer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface RendererRepository extends JpaRepository<Renderer, String>, PagingAndSortingRepository<Renderer, String>, JpaSpecificationExecutor<Renderer> {
}