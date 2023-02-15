package com.thot.server.controller.dto;

import lombok.Data;

import java.util.HashMap;

@Data
public class PrintRequest {
    private HashMap<String, Object> parameters;
    private String clientIdentifier;
    private String printService;
}
