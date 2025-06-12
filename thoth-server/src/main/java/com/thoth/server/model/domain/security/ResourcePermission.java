package com.thoth.server.model.domain.security;

import com.thoth.server.controller.view.ResourcePermissionView;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class ResourcePermission {

    private String identifier;
    private Permission permission;

    public ResourcePermissionView toView(){
        ResourcePermissionView view = new ResourcePermissionView();
        view.setIdentifier(this.identifier);
        view.setPermission(this.permission);
        return view;
    }
}
