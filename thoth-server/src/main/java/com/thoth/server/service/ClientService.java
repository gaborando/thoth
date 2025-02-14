package com.thoth.server.service;

import com.thoth.common.dto.ClientQueueMode;
import com.thoth.server.beans.IAuthenticationFacade;
import com.thoth.server.controller.view.PrintingRequestView;
import com.thoth.server.model.domain.*;
import com.thoth.server.model.domain.security.Permission;
import com.thoth.server.model.domain.security.ResourcePermission;
import com.thoth.server.model.repository.ClientRepository;
import com.thoth.server.model.repository.PrintingRequestRepository;
import jakarta.persistence.EntityManager;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import com.thoth.common.dto.PrintRequest;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.*;

@Service
public class ClientService {

    private final ClientRepository repository;


    private final EntityManager entityManager;

    private final RabbitTemplate rabbitTemplate;

    private final PrintingRequestRepository printingRequestRepository;

    private final IAuthenticationFacade authenticationFacade;

    private final LockRegistry lockRegistry;

    public ClientService(ClientRepository repository, RabbitTemplate rabbitTemplate, PrintingRequestRepository printingRequestRepository, IAuthenticationFacade authenticationFacade, LockRegistry lockRegistry, EntityManager entityManager) {
        this.repository = repository;
        this.rabbitTemplate = rabbitTemplate;
        this.printingRequestRepository = printingRequestRepository;
        this.authenticationFacade = authenticationFacade;
        this.lockRegistry = lockRegistry;
        this.entityManager = entityManager;
    }

    public Client register(String identifier, String name, String ownerSID, List<String> printServices, ClientQueueMode queueMode, String userSID) {
        var c = repository.findById(identifier).orElseGet(() -> {
            var cc = new Client();
            cc.setAllowedUserList(new ArrayList<>());
            return cc;
        });
        c.setName(name);
        c.setIdentifier(identifier);
        c.setCreatedBy(ownerSID);
        c.setPrintServices(printServices);
        c.setCreatedAt(Instant.now());
        c.setQueueMode(queueMode);
        if (c.getAllowedUserList().stream().noneMatch(p -> p.getSid().equals(userSID))
                && !ownerSID.equals(userSID)) {
            var p = new ResourcePermission();
            p.setPermission(Permission.W);
            p.setSid(userSID);
            c.getAllowedUserList().add(p);
        }
        return repository.save(c);
    }

    @PreAuthorize("@authenticationFacade.canWrite(#client)")
    public void delete(Client client) {
        repository.delete(client);
    }

    public void printSvg(String clientIdentifier, String printingService, String svg, Integer copies, Object resource, HashMap<String, Object> parameters) {

        var client = repository.findById(clientIdentifier).orElseThrow();
        var requestId = clientIdentifier + '_' + UUID.randomUUID();
        if (client.getQueueMode() == ClientQueueMode.AMQP) {
            var request = new PrintRequest();
            request.setIdentifier(requestId);
            request.setPrintService(printingService);
            request.setSvg(svg);
            request.setCopies(copies);
            var ex = new DirectExchange("thoth." + clientIdentifier + ".rpc");
            rabbitTemplate.convertSendAndReceive(ex.getName(), "rpc", request);
        } else if (client.getQueueMode() == ClientQueueMode.THOTH) {
            var pr = new PrintingRequest();
            pr.setIdentifier(requestId);
            pr.setPrintService(printingService);
            pr.setClient(repository.findById(clientIdentifier).orElseThrow());
            pr.setCreatedAt(ZonedDateTime.now());
            pr.setCreatedBy(authenticationFacade.getUserSID());
            pr.setStatus(PrintingRequestStatus.PENDING);
            pr.setCopies(copies);
            pr.setSvg(svg);
            if (resource instanceof Template t) {
                pr.setTemplate(t);
            } else if (resource instanceof Renderer r) {
                pr.setRenderer(r);
            } else {
                throw new RuntimeException("Resource must be either a Template or a Renderer");
            }
            printingRequestRepository.save(pr);
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                var status = printingRequestRepository.getStatus(pr.getIdentifier());
                if (status == PrintingRequestStatus.COMPLETED) {
                    return;
                } else if (status == PrintingRequestStatus.FAILED) {
                    var error = printingRequestRepository.getError(pr.getIdentifier());
                    throw new RuntimeException(error);
                }
            }
        } else {
            throw new RuntimeException("Queue mode not supported");
        }
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

    @PreAuthorize("@authenticationFacade.canWrite(#client)")
    public List<PrintingRequestView> consumePendingRequests(Client client) {
        if (client.getQueueMode() != ClientQueueMode.THOTH) {
            throw new RuntimeException("Queue mode not supported");
        }
        var lock = lockRegistry.obtain("client_"  +client.getIdentifier() + "_lock");
        lock.lock();
        try{
            var requests = printingRequestRepository.findAllByClientAndStatus(client, PrintingRequestStatus.PENDING,
                    PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "createdAt")));
            for (var r : requests) {
                r.setStatus(PrintingRequestStatus.IN_PROGRESS);
                r.setDequeuedAt(ZonedDateTime.now());
                printingRequestRepository.save(r);
            }
            return requests.stream().map(e -> e.toView(authenticationFacade.getUserSID(), authenticationFacade.getOrganizationSID())).toList();
        }finally {
            lock.unlock();
        }
    }

    @PreAuthorize("@authenticationFacade.canWrite(#client)")
    public PrintingRequestView setPrintingRequestStatus(Client client, String request, boolean hasError, String errorMessage) {
        if (client.getQueueMode() != ClientQueueMode.THOTH) {
            throw new RuntimeException("Queue mode not supported");
        }
        var pr = printingRequestRepository.findById(request).orElseThrow();
        pr.setStatus(hasError ? PrintingRequestStatus.FAILED : PrintingRequestStatus.COMPLETED);
        pr.setExecutedAt(ZonedDateTime.now());
        pr.setErrorMessage(errorMessage);
        printingRequestRepository.save(pr);
        return pr.toView(authenticationFacade.getUserSID(), authenticationFacade.getOrganizationSID());
    }
}
