package com.thoth.server.controller;

import com.thoth.server.beans.AuthenticationFacade;
import com.thoth.server.controller.dto.secret.SecretCreateRequest;
import com.thoth.server.controller.view.SecretView;
import com.thoth.server.controller.view.SecuredResourceView;
import com.thoth.server.controller.view.TemplateView;
import com.thoth.server.model.domain.Secret;
import com.thoth.server.model.domain.Template;
import com.thoth.server.model.domain.security.SecuredResource;
import com.thoth.server.service.SecretService;
import io.github.perplexhub.rsql.RSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.xml.parsers.ParserConfigurationException;

@RestController
@RequestMapping("/secret")
public class SecretController {

    private final SecretService secretService;

    public SecretController(SecretService secretService) {
        this.secretService = secretService;
    }

    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({"ROLE_USER", "ROLE_API"})
    public ResponseEntity<Page<SecretView>> findAll(
            @RequestParam(value = "filter", defaultValue = "") String filter,
            @RequestParam(value = "sort", defaultValue = "createdAt,desc") String sort,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "15") int size,
            AuthenticationFacade facade

    ) {

        Specification<Secret> f = RSQLJPASupport.toSpecification(filter);
        f = f.and(RSQLJPASupport.toSort(sort));
        return ResponseEntity.ok(secretService.search(f
                        , PageRequest.of(page, size))
                .map(e -> e.toView(facade.getUserSID(), facade.getOrganizationSID())));
    }

    @PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({"ROLE_USER"})
    public ResponseEntity<SecretView> create(@RequestBody SecretCreateRequest request,
                                               AuthenticationFacade facade) {
        return ResponseEntity.ok(secretService.create(request.getName(), request.getValue()).toView(facade.getUserSID(),
                facade.getOrganizationSID()));
    }

    @PutMapping(value = "/{identifier}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({"ROLE_USER"})
    public ResponseEntity<SecretView> update(@RequestBody SecuredResource resource,
                                               @PathVariable String identifier,
                                               AuthenticationFacade facade) throws ParserConfigurationException {
        return ResponseEntity.ok(secretService.update(
                        secretService.getById(identifier).orElseThrow(),
                        resource.getAllowedUserList(),
                        resource.getAllowedOrganizationList())
                .toView(facade.getUserSID(), facade.getOrganizationSID()));
    }

    @DeleteMapping("/{identifier}")
    @Secured({"ROLE_USER"})
    public void delete(@PathVariable String identifier) {
        var e = secretService.getById(identifier).orElseThrow();
        secretService.delete(e);
    }
}
