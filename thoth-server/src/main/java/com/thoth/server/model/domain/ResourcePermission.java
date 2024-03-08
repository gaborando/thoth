package com.thoth.server.model.domain;

import com.thoth.server.controller.view.ResourcePermissionView;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class ResourcePermission {

    private String sid;
    private Permission permission;

    public ResourcePermissionView toView(){
        ResourcePermissionView view = new ResourcePermissionView();
        view.setSid(this.sid);
        view.setPermission(this.permission);
        return view;
    }
}
