package com.thoth.server.controller;

import com.thoth.server.model.domain.Client;
import com.thoth.server.service.ClientService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/client")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<Client>> findAll(
            @RequestParam(defaultValue = "0") int page
    ) {
        return ResponseEntity.ok(clientService.search(Specification.where(null),
                PageRequest.of(page, 10)));
    }

    @DeleteMapping("/{identifier}")
    public void unregister(
            @PathVariable String identifier) {
        clientService.deleteById(identifier);
    }

}
