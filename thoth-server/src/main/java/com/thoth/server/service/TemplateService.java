package com.thoth.server.service;

import com.thoth.server.model.domain.Template;
import com.thoth.server.model.repository.TemplateRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class TemplateService {

    private final TemplateRepository templateRepository;

    public TemplateService(TemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    public Template save(Template template){
        return templateRepository.save(template);
    }

    public void deleteById(String id){
        templateRepository.deleteById(id);
    }

    public Page<Template> search(Specification<Template> specification, Pageable pageable){
        return templateRepository.findAll(specification, pageable);
    }

    public Template create(String name) {
        var template = new Template();
        template.setId("tpl_" + UUID.randomUUID().toString());
        template.setName(name);
        return templateRepository.save(template);
    }

    public Optional<Template> getById(String identifier) {
        return templateRepository.findById(identifier);
    }
}
