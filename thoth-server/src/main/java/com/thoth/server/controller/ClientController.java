package com.thoth.server.controller;

import com.thoth.server.beans.AuthenticationFacade;
import com.thoth.server.controller.view.ClientView;
import com.thoth.server.model.domain.Client;
import com.thoth.server.service.ClientService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/client")
@Secured("ROLE_USER")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @Secured({"ROLE_USER", "ROLE_API"})
    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<ClientView>> findAll(
            @RequestParam(defaultValue = "0") int page,
            AuthenticationFacade facade
    ) {
        return ResponseEntity.ok(clientService.search(Specification.where(null),
                PageRequest.of(page, 30, Sort.by(Sort.Order.desc("createdAt")))).map(u -> u.toView(
                        facade.getUserSID(),
                facade.getOrganizationSID()
        )));
    }

    @PutMapping("/{identifier}")
    public ResponseEntity<ClientView> update(
            @PathVariable String identifier, @RequestBody Client client,
            AuthenticationFacade facade) {
       return ResponseEntity.ok(clientService.update(
               clientService.findById(identifier).orElseThrow(), client)
               .toView(facade.getUserSID(), facade.getOrganizationSID()));
    }

    @DeleteMapping("/{identifier}")
    public void unregister(
            @PathVariable String identifier) {
        clientService.delete(clientService.findById(identifier).orElseThrow());
    }

}
