package com.thoth.server.controller.dto.renderer;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class RendererCreateRequest {
    private String name;
    private String template;
    private List<String> datasource;
    private Map<String, Association> associationMap;
}
