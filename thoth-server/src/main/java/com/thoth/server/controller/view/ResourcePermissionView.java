package com.thoth.server.controller.view;

import com.thoth.server.model.domain.security.Permission;
import lombok.Data;

@Data
public class ResourcePermissionView {
    private String identifier;
    private Permission permission;
}
