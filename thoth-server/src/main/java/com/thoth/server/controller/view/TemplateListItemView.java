package com.thoth.server.controller.view;

import com.thoth.server.model.domain.security.Permission;
import lombok.Data;

@Data
public class TemplateListItemView {

    private String id;
    private String name;
    private String img;
    private Permission permission;
}
