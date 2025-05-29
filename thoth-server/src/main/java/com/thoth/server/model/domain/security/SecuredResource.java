package com.thoth.server.model.domain.security;

import com.thoth.server.controller.view.ResourcePermissionView;
import com.thoth.server.controller.view.SecuredResourceView;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
@MappedSuperclass
public abstract class SecuredResource {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_by_id", updatable = false)
    private User createdBy;

    @Column(name = "organization_id", updatable = false)
    private Long organizationId;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<ResourcePermission> allowedUserList;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<ResourcePermission> allowedOrganizationList;

    public Permission checkPermission(String uSid, String oSid, boolean isOrganizationAdmin) {
        // If user is the creator, they have write permission
        if (createdBy != null && Objects.equals(uSid, createdBy.getUsername())) return Permission.W;

        // If user is an organization admin and the resource belongs to their organization,
        // they have write permission
        if (isOrganizationAdmin && organizationId != null && Objects.equals(organizationId.toString(), oSid)) {
            return Permission.W;
        }

        // Check user-specific permissions
        var canRead = false;
        if (allowedUserList != null) {
            for (var p : allowedUserList) {
                if (Objects.equals(uSid, p.getSid())) {
                    if (p.getPermission() == Permission.W) {
                        return Permission.W;
                    }
                    if (p.getPermission() == Permission.R) {
                        canRead = true;
                        break;
                    }
                }
            }
        }

        // Check organization-specific permissions
        if (allowedOrganizationList != null) {
            for (var p : allowedOrganizationList) {
                if (Objects.equals(oSid, p.getSid())) {
                    if (p.getPermission() == Permission.W) {
                        return Permission.W;
                    }
                    if (p.getPermission() == Permission.R) {
                        canRead = true;
                        break;
                    }
                }
            }
        }

        // If the resource belongs to the user's organization, they can read it
        if (!canRead && organizationId != null && Objects.equals(organizationId.toString(), oSid)) {
            canRead = true;
        }

        return canRead ? Permission.R : null;

    }

    public void setView(SecuredResourceView view, String uSid, String oSid) {
        view.setCreatedBy(this.createdBy != null ? this.createdBy.getUsername() : null);
        view.setOrganizationId(this.organizationId);
        List<ResourcePermissionView> allowedUserListView = new ArrayList<>();
        if (this.allowedUserList != null)
            for (ResourcePermission permission : this.allowedUserList) {
                allowedUserListView.add(permission.toView());
            }
        view.setAllowedUserList(allowedUserListView);
        List<ResourcePermissionView> allowedOrganizationListView = new ArrayList<>();

        if (this.allowedOrganizationList != null)
            for (ResourcePermission permission : this.allowedOrganizationList) {
                allowedOrganizationListView.add(permission.toView());
            }
        view.setAllowedOrganizationList(allowedOrganizationListView);
        // For now, assume the user is not an organization admin
        // This will be updated in the AuthenticationFacade
        view.setPermission(checkPermission(uSid, oSid, false));
    }
}
