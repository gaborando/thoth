package com.thoth.server.service;

import com.thoth.server.beans.IAuthenticationFacade;
import com.thoth.server.controller.dto.renderer.Association;
import com.thoth.server.model.domain.Renderer;
import com.thoth.server.model.domain.ResourcePermission;
import com.thoth.server.model.domain.datasource.DatasourceProperties;
import com.thoth.server.model.repository.DatasourcePropertiesRepository;
import com.thoth.server.model.repository.RendererRepository;
import com.thoth.server.model.repository.TemplateRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RendererService {

    private final TemplateRepository templateRepository;
    private final DatasourcePropertiesRepository datasourcePropertiesRepository;

    private final RendererRepository rendererRepository;

    private final IAuthenticationFacade facade;

    public RendererService(TemplateRepository templateRepository, DatasourcePropertiesRepository datasourcePropertiesRepository, RendererRepository rendererRepository, IAuthenticationFacade facade) {
        this.templateRepository = templateRepository;
        this.datasourcePropertiesRepository = datasourcePropertiesRepository;
        this.rendererRepository = rendererRepository;
        this.facade = facade;
    }

    public Renderer create(String name, String template, List<String> datasource, Map<String, Association> associationMap) {
        var renderer = new Renderer();
        renderer.setId("rndr_" + UUID.randomUUID());
        renderer.setName(name);
        renderer.setTemplate(templateRepository.findById(template).orElseThrow());
        renderer.setDatasourceProperties(datasource.stream()
                .map(datasourcePropertiesRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet()));
        renderer.setAssociationMap(associationMap);
        renderer.setCreatedAt(Instant.now());
        facade.fillSecuredResource(renderer);
        return rendererRepository.save(renderer);

    }

    public Page<Renderer> search(Specification<Renderer> specification, PageRequest pageRequest) {
        return rendererRepository.findAll(facade.securedSpecification(specification, Renderer.class), pageRequest);
    }


    @PreAuthorize("@authenticationFacade.canWrite(#renderer)")
    public void delete(Renderer renderer) {
        rendererRepository.delete(renderer);
    }

    @PostAuthorize("@authenticationFacade.canRead(returnObject) || hasRole('ROLE_TMP')")
    public Optional<Renderer> findById(String identifier) {
        return rendererRepository.findById(identifier);
    }

    @PreAuthorize("@authenticationFacade.canWrite(#original)")
    public Renderer update(String s, String name, List<String> datasource, Map<String, Association> associationMap, List<ResourcePermission> allowedUserList, List<ResourcePermission> allowedOrganizationList) {
        var original = rendererRepository.findById(s).orElseThrow();
        original.setAssociationMap(associationMap);
        original.setDatasourceProperties(
                new HashSet<>(datasourcePropertiesRepository
                        .findAllById(datasource)));
        original.setName(name);
        original.setAllowedOrganizationList(allowedOrganizationList);
        original.setAllowedUserList(allowedUserList);
        return rendererRepository.save(original);
    }
}
