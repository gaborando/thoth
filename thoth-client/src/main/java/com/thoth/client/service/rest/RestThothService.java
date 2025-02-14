package com.thoth.client.service.rest;


import com.thoth.client.ClientService;
import com.thoth.client.service.ThothService;
import com.thoth.common.dto.PrintRequest;
import com.thoth.common.dto.PrintRequestResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Component
@ConditionalOnProperty(name = "thoth.queue.mode", havingValue = "THOTH")
public class RestThothService implements ThothService {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String apiKey;
    private final ClientService clientService;
    private final long polling;
    private static final Logger logger = LogManager.getLogger(RestThothService.class);

    public RestThothService(
            @Value("${thoth.rest.polling:5000}") long polling,
            @Value("${thoth.rest.base.url}") String baseUrl,
            @Value("${thoth.rest.api.key}") String apiKey, ClientService clientService

    ) {
        this.clientService = clientService;
        this.restTemplate = new RestTemplate();
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.polling = polling;
    }

    public void startConsumption() {
        var failures = 0;
        while (!Thread.currentThread().isInterrupted()) {
            try {
                var start = System.currentTimeMillis();
                var requests = restTemplate.getForObject(baseUrl + "/client/" + clientService.getClientIdentifier()
                                + "/consume-pending-requests/?API_KEY=" + apiKey,
                        PrintRequest[].class);
                if (requests != null) {
                    for (PrintRequest request : requests) {
                        var e = clientService.printSvg(request);
                        var resp = new PrintRequestResponse();
                        resp.setHasError(e != null);
                        resp.setErrorMessage(e != null ? e.getMessage() : null);
                        restTemplate.postForEntity(baseUrl + "/client/" +
                                        clientService.getClientIdentifier() + "/requests/" +
                                        request.getIdentifier() + "/status?API_KEY=" + apiKey, resp,
                                PrintRequest.class);

                    }
                }
                var end = System.currentTimeMillis();
                try {
                    var diff = end - start;
                    if (diff < polling) {
                        Thread.sleep(polling - diff);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                failures = 0;
            } catch (Exception e) {
                logger.error("Error on consuming pending requests", e);
                failures++;
                try {
                    Thread.sleep(polling);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
                if (failures > 10) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    @Override
    public void registerClient() {

        clientService.register(r -> {
            restTemplate.postForEntity(baseUrl + "/client/?API_KEY=" + apiKey, r,
                    Object.class);
        });

    }
}
