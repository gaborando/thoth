package com.thoth.server.controller;

import com.thoth.server.controller.dto.apikey.ApiKeyCreateRequest;
import com.thoth.server.model.domain.ApiKey;
import com.thoth.server.model.domain.Client;
import com.thoth.server.service.ApiKeyService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api-key")
@Secured("ROLE_USER")
public class ApiKeyController {


    private final ApiKeyService apiKeyService;

    public ApiKeyController(ApiKeyService apiKeyService) {
        this.apiKeyService = apiKeyService;
    }

    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<ApiKey>> findAll(
            @RequestParam(defaultValue = "0") int page
    ) {
        return ResponseEntity.ok(apiKeyService.search(Specification.allOf(),
                PageRequest.of(page, 10, Sort.by(Sort.Order.asc("createdAt")))).map(k -> {
                    k.setApiKey(null);
                    return k;
        }));
    }

    @PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiKey> create(@RequestBody ApiKeyCreateRequest request) {
        return ResponseEntity.ok(apiKeyService.create(
                request.getName(),
                request.getUserSID(),
                request.getOrganizationSID(),
                request.getExpiry()));
    }

    @DeleteMapping(value = "/{identifier}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void delete(@PathVariable String identifier) {
        apiKeyService.delete(identifier);
    }
}
