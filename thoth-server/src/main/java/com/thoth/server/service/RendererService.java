package com.thoth.server.service;

import com.thoth.server.controller.dto.renderer.Association;
import com.thoth.server.model.domain.Renderer;
import com.thoth.server.model.repository.DatasourcePropertiesRepository;
import com.thoth.server.model.repository.RendererRepository;
import com.thoth.server.model.repository.TemplateRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class RendererService {

    private final TemplateRepository templateRepository;
    private final DatasourcePropertiesRepository datasourcePropertiesRepository;

    private final RendererRepository rendererRepository;

    public RendererService(TemplateRepository templateRepository, DatasourcePropertiesRepository datasourcePropertiesRepository, RendererRepository rendererRepository) {
        this.templateRepository = templateRepository;
        this.datasourcePropertiesRepository = datasourcePropertiesRepository;
        this.rendererRepository = rendererRepository;
    }

    public Renderer create(String name, String template, List<String> datasource, Map<String, Association> associationMap) {
        var renderer = new Renderer();
        renderer.setId("rndr_" + UUID.randomUUID());
        renderer.setName(name);
        renderer.setTemplate(templateRepository.findById(template).orElseThrow());
        renderer.setDatasourceProperties(datasourcePropertiesRepository.findAllById(datasource));
        renderer.setAssociationMap(associationMap);
        return rendererRepository.save(renderer);

    }

    public Page<Renderer> search(Specification<Renderer> specification, PageRequest pageRequest) {
        return rendererRepository.findAll(specification, pageRequest);
    }

    public Renderer save(Renderer renderer) {
        return rendererRepository.save(renderer);
    }

    public void deleteById(String identifier) {
        rendererRepository.deleteById(identifier);
    }

    public Optional<Renderer> findById(String identifier) {
        return rendererRepository.findById(identifier);
    }
}
