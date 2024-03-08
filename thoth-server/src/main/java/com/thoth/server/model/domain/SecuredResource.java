package com.thoth.server.model.domain;

import com.thoth.server.controller.view.ResourcePermissionView;
import com.thoth.server.controller.view.SecuredResourceView;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
@MappedSuperclass
public abstract class SecuredResource {
    @Column(updatable = false)
    private String createdBy;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<ResourcePermission> allowedUserList;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<ResourcePermission> allowedOrganizationList;

    public Permission checkPermission(String uSid, String oSid) {
        if (Objects.equals(uSid, createdBy)) return Permission.W;
        var canRead = false;
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
        return canRead ? Permission.R : null;

    }

    public void setView(SecuredResourceView view, String uSid, String oSid) {
        view.setCreatedBy(this.createdBy);
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
        view.setPermission(checkPermission(uSid, oSid));
    }
}
