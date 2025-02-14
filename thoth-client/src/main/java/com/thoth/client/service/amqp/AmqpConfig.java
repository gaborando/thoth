package com.thoth.client.service.amqp;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;


@Configuration
@EnableAsync
@ConditionalOnProperty(name = "thoth.queue.mode", havingValue = "AMQP")
public class AmqpConfig {

    @Bean
    public Queue queue(@Value("${thoth.client.identifier}") String clientIdentifier) {
        return new Queue("thoth." + clientIdentifier + ".rpc.requests");
    }

    @Bean
    public DirectExchange exchange(@Value("${thoth.client.identifier}") String clientIdentifier) {
        return new DirectExchange("thoth." + clientIdentifier + ".rpc");
    }


    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Binding binding(DirectExchange exchange,
                           Queue queue) {
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with("rpc");
    }


}
