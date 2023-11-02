package com.thoth.server.controller.dto.renderer;

import com.thoth.server.model.domain.ResourcePermission;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public class RendererUpdateRequest {
    private String name;
    private List<String> datasource;
    private Map<String, Association> associationMap;
    private List<ResourcePermission> allowedUserList;
    private List<ResourcePermission> allowedOrganizationList;
}
