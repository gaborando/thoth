package com.thoth.client.service.amqp;


import com.thoth.client.ClientService;
import com.thoth.client.service.ThothService;
import com.thoth.common.dto.PrintRequest;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "thoth.queue.mode", havingValue = "AMQP")
public class AmqpThothService implements ThothService {

    private final ClientService clientService;
    private final RabbitTemplate rabbitTemplate;

    private final DirectExchange serverExchange;

    public AmqpThothService(ClientService clientService, RabbitTemplate rabbitTemplate,
                            @Value("${thoth.server.exchange}") String serverExchange) {
        this.clientService = clientService;
        this.rabbitTemplate = rabbitTemplate;
        this.serverExchange = new DirectExchange(serverExchange);
    }

    @RabbitListener(queues = "thoth.${thoth.client.identifier}.rpc.requests")
    public Boolean printSvg(PrintRequest printRequest) {
        return clientService.printSvg(printRequest) == null;
    }

    public void registerClient(){
        clientService.register(r -> {
            rabbitTemplate.convertAndSend(serverExchange.getName(), "rpc", r);
        });

    }
}
