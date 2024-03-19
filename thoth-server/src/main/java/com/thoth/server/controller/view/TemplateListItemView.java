package com.thoth.server.controller.view;

import com.thoth.server.model.domain.security.Permission;
import lombok.Data;

import java.util.Set;

@Data
public class TemplateListItemView {

    private String id;
    private String name;
    private String img;
    private Set<String> markers;
    private Permission permission;
}
