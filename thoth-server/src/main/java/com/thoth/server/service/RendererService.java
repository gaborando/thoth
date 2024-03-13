package com.thoth.server.service;

import com.thoth.server.beans.IAuthenticationFacade;
import com.thoth.server.controller.view.AssociationView;
import com.thoth.server.model.domain.Renderer;
import com.thoth.server.model.domain.security.ResourcePermission;
import com.thoth.server.model.repository.DatasourcePropertiesRepository;
import com.thoth.server.model.repository.RendererRepository;
import com.thoth.server.model.repository.TemplateRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

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

    public Renderer create(String name, String template, List<String> datasource, Map<String, AssociationView> associationMap
            , Map<String, AssociationView> parametersMap) {
        var renderer = new Renderer();
        renderer.setId("rndr_" + UUID.randomUUID());
        renderer.setName(name);
        renderer.setTemplate(templateRepository.findById(template).orElseThrow());
        renderer.setDatasourceProperties(new HashSet<>(datasourcePropertiesRepository.findAllById(datasource)));
        renderer.setAssociationMap(associationMap);
        renderer.setParametersMap(parametersMap);
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

    public Optional<Renderer> findByIdUnsafe(String identifier) {
        return rendererRepository.findById(identifier);
    }



    @PreAuthorize("@authenticationFacade.canWrite(#original)")
    public Renderer update(Renderer original, String name, List<String> datasource, Map<String, AssociationView> associationMap
            , Map<String, AssociationView> parametersMap, List<ResourcePermission> allowedUserList, List<ResourcePermission> allowedOrganizationList) {
        original.setAssociationMap(associationMap);
        original.setParametersMap(parametersMap);
        original.setDatasourceProperties(
                new HashSet<>(datasourcePropertiesRepository
                        .findAllById(datasource)));
        original.setName(name);
        original.setAllowedOrganizationList(allowedOrganizationList);
        original.setAllowedUserList(allowedUserList);
        return rendererRepository.save(original);
    }
}
