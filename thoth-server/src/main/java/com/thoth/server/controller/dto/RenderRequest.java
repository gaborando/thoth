package com.thoth.server.controller.dto;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class RenderRequest {
    private String identifier;
    private HashMap<String, Object> parameters;
}
