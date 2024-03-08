package com.thoth.server.controller.dto.renderer;

import com.thoth.server.controller.view.AssociationView;
import com.thoth.server.model.domain.ResourcePermission;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class RendererUpdateRequest {
    private String name;
    private List<String> datasource;
    private Map<String, AssociationView> associationMap;
    private Map<String, AssociationView> parametersMap;
    private List<ResourcePermission> allowedUserList;
    private List<ResourcePermission> allowedOrganizationList;
}
