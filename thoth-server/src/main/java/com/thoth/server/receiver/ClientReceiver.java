package com.thoth.server.receiver;

import com.thoth.server.service.ClientService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.thoth.common.dto.RegisterClientRequest;

@Service
public class ClientReceiver {

    private final ClientService clientService;

    public ClientReceiver(ClientService clientService) {
        this.clientService = clientService;
    }


    @RabbitListener(queues = "thoth.server.rpc.requests")
    public void register(RegisterClientRequest request) {
        clientService.register(request.getIdentifier(), request.getName(), request.getOwnerSID(),
                request.getPrintServices());
    }
}
