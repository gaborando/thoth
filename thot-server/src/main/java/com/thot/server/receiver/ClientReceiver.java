package com.thot.server.receiver;

import com.thot.server.service.ClientService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.thot.common.dto.RegisterClientRequest;

@Service
public class ClientReceiver {

    private final ClientService clientService;

    public ClientReceiver(ClientService clientService) {
        this.clientService = clientService;
    }


    @RabbitListener(queues = "thot.server.rpc.requests")
    public void register(RegisterClientRequest request) {
        clientService.register(request.getIdentifier(), request.getName(),
                request.getPrintServices());
    }
}
