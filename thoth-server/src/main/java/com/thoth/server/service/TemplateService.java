package com.thoth.server.service;

import com.thoth.server.beans.IAuthenticationFacade;
import com.thoth.server.model.domain.Template;
import com.thoth.server.model.repository.TemplateRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class TemplateService {

    private final TemplateRepository templateRepository;
    private final IAuthenticationFacade authenticationFacade;

    public TemplateService(TemplateRepository templateRepository, IAuthenticationFacade authenticationFacade) {
        this.templateRepository = templateRepository;
        this.authenticationFacade = authenticationFacade;
    }

    @PreAuthorize("@authenticationFacade.canAccess(#template)")
    public Template save(Template template){
        return templateRepository.save(template);
    }

    @PreAuthorize("@authenticationFacade.canAccess(#template)")
    public void delete(Template template){
        templateRepository.delete(template);
    }

    public Page<Template> search(Specification<Template> specification, Pageable pageable){
        return templateRepository.findAll(authenticationFacade.securedSpecification(specification, Template.class), pageable);
    }

    public Template create(String name) {
        var template = new Template();
        template.setId("tpl_" + UUID.randomUUID().toString());
        template.setName(name);
        template.setCreatedAt(Instant.now());
        authenticationFacade.fillSecuredResource(template);
        return templateRepository.save(template);
    }

    @PostAuthorize("@authenticationFacade.canAccess(returnObject)")
    public Optional<Template> getById(String identifier) {
        return templateRepository.findById(identifier);
    }
}
