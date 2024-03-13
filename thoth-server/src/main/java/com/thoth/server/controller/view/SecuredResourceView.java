package com.thoth.server.controller.view;

import com.thoth.server.model.domain.security.Permission;
import lombok.Data;

import java.util.List;

@Data
public class SecuredResourceView {
    private String createdBy;
    private List<ResourcePermissionView> allowedUserList;
    private List<ResourcePermissionView> allowedOrganizationList;
    private Permission permission;
}
