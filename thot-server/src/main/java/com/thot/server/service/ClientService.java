package com.thot.server.service;

import com.thot.server.model.domain.Client;
import com.thot.server.model.repository.ClientRepository;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.thot.common.dto.PrintRequest;

import java.util.List;

@Service
public class ClientService {

    private final ClientRepository repository;

    private final RabbitTemplate rabbitTemplate;

    public ClientService(ClientRepository repository, RabbitTemplate rabbitTemplate) {
        this.repository = repository;
        this.rabbitTemplate = rabbitTemplate;
        //rabbitTemplate.setReceiveTimeout(60*1000);
        //rabbitTemplate.setReplyTimeout(60*1000);
    }

    public Client register(String identifier, String name, List<String> printServices){
        var c = new Client();
        c.setName(name);
        c.setIdentifier(identifier);
        c.setPrintServices(printServices);
        return repository.save(c);
    }

    public void deleteById(String identifier){
        repository.deleteById(identifier);
    }

    public void printSvg(String clientIdentifier, String printingService, String svg){
        var request = new PrintRequest();
        request.setPrintService(printingService);
        request.setSvg(svg);
        var ex = new DirectExchange("thot."+clientIdentifier+".rpc");
        rabbitTemplate.convertSendAndReceive(ex.getName(), "rpc", request);
    }

    public Page<Client> search(Specification<Client> where, PageRequest of) {
        return repository.findAll(where, of);
    }
}
