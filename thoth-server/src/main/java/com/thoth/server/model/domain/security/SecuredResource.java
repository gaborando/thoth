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

    @ElementCollection(fetch = FetchType.EAGER)
    private List<ResourcePermission> allowedUserList;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<ResourcePermission> allowedGroupList;

    public Permission checkPermission(User user) {
        // If user is the creator, they have write permission
        if (createdBy != null && Objects.equals(user.getUsername(), createdBy.getUsername())) return Permission.W;

        // If user is an organization admin and the resource belongs to their organization,
        // they have write permission
        if (isOrganizationAdmin && getOrganizationId() != null && Objects.equals(getOrganizationId(), oSid)) {
            return Permission.W;
        }

        // Check user-specific permissions
        var canRead = false;
        if (allowedUserList != null) {
            for (var p : allowedUserList) {
                if (Objects.equals(user.getUsername(), p.getIdentifier())) {
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
        if (allowedGroupList != null) {
            for (var p : allowedGroupList) {
                for (UserGroupAssociation group : user.getGroups()) {
                    if (Objects.equals(group.getGroup().getIdentifier(), p.getIdentifier())) {
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
        }

        // If the resource belongs to the user's organization, they can read it
        if (!canRead && getOrganizationId() != null && Objects.equals(getOrganizationId(), oSid)) {
            canRead = true;
        }

        return canRead ? Permission.R : null;

    }

    public void setView(SecuredResourceView view, User user) {
        view.setCreatedBy(this.createdBy != null ? this.createdBy.getUsername() : null);
        view.setOrganizationId(this.getOrganizationId());
        List<ResourcePermissionView> allowedUserListView = new ArrayList<>();
        if (this.allowedUserList != null)
            for (ResourcePermission permission : this.allowedUserList) {
                allowedUserListView.add(permission.toView());
            }
        view.setAllowedUserList(allowedUserListView);
        List<ResourcePermissionView> allowedOrganizationListView = new ArrayList<>();

        if (this.allowedGroupList != null)
            for (ResourcePermission permission : this.allowedGroupList) {
                allowedOrganizationListView.add(permission.toView());
            }
        view.setAllowedOrganizationList(allowedOrganizationListView);
        // For now, assume the user is not an organization admin
        // This will be updated in the AuthenticationFacade
        view.setPermission(checkPermission(user));
    }
}
