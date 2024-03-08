package com.thoth.server.controller.view;

import com.thoth.server.model.domain.Permission;
import lombok.Data;

@Data
public class RendererListItemView {
    private String id;
    private String name;
    private Permission permission;
}
