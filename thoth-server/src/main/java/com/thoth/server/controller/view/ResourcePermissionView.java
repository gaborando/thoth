package com.thoth.server.controller.view;

import com.thoth.server.model.domain.Permission;
import com.thoth.server.model.domain.ResourcePermission;
import lombok.Data;

@Data
public class ResourcePermissionView {
    private String sid;
    private Permission permission;
}
