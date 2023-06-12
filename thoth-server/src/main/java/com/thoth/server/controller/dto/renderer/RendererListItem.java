package com.thoth.server.controller.dto.renderer;

import com.thoth.server.model.domain.Renderer;

public class RendererListItem {
    private String id;
    private String name;
    public RendererListItem(Renderer renderer) {
        this.id = renderer.getId();
        this.name = renderer.getName();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
