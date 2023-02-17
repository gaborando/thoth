package com.thoth.server.controller.dto;

import lombok.Data;

import java.util.HashMap;

@Data
public class PrintRequest {
    private HashMap<String, Object> parameters;
    private String clientIdentifier;
    private String printService;

    private Integer copies;
}
