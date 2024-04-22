package com.thoth.server.service;

import com.thoth.server.beans.IAuthenticationFacade;
import com.thoth.server.model.domain.Client;
import com.thoth.server.model.repository.ClientRepository;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.thoth.common.dto.PrintRequest;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class ClientService {

    private final ClientRepository repository;

    private final RabbitTemplate rabbitTemplate;

    private final IAuthenticationFacade authenticationFacade;
    public ClientService(ClientRepository repository, RabbitTemplate rabbitTemplate, IAuthenticationFacade authenticationFacade) {
        this.repository = repository;
        this.rabbitTemplate = rabbitTemplate;
        this.authenticationFacade = authenticationFacade;
    }

    public Client register(String identifier, String name, String ownerSID, List<String> printServices){
        var c = repository.findById(identifier).orElse(new Client());
        c.setName(name);
        c.setIdentifier(identifier);
        c.setCreatedBy(ownerSID);
        c.setPrintServices(printServices);
        c.setCreatedAt(Instant.now());
        try {
            return repository.save(c);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @PreAuthorize("@authenticationFacade.canWrite(#client)")
    public void delete(Client client){
        repository.delete(client);
    }

    public void printSvg(String clientIdentifier, String printingService, String svg, Integer copies){
        var request = new PrintRequest();
        request.setPrintService(printingService);
        request.setSvg(svg);
        request.setCopies(copies);
        var ex = new DirectExchange("thoth."+clientIdentifier+".rpc");
        rabbitTemplate.convertSendAndReceive(ex.getName(), "rpc", request);
    }

    public Page<Client> search(Specification<Client> where, PageRequest of) {
        return repository.findAll(authenticationFacade.securedSpecification(where, Client.class), of);
    }

    @PostAuthorize("@authenticationFacade.canRead(returnObject)")
    public Optional<Client> findById(String identifier) {
        return repository.findById(identifier);
    }


    @PreAuthorize("@authenticationFacade.canWrite(#original)")
    public Client update(Client original, Client update) {
        original.setAllowedUserList(update.getAllowedUserList());
        original.setAllowedOrganizationList(update.getAllowedOrganizationList());
        return repository.save(original);
    }
}
