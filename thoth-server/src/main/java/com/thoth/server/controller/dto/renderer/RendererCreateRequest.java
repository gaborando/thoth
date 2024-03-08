package com.thoth.server.controller.dto.renderer;

import com.thoth.server.controller.view.AssociationView;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class RendererCreateRequest {
    private String name;
    private String template;
    private List<String> datasource;
    private Map<String, AssociationView> associationMap;
    private Map<String, AssociationView> parametersMap;
}
