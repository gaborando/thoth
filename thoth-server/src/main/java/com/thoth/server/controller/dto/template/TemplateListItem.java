package com.thoth.server.controller.dto.template;

import com.thoth.server.model.domain.Renderer;
import com.thoth.server.model.domain.Template;

public class TemplateListItem {

    private String id;
    private String name;

    private String img;
    public TemplateListItem(Template item) {
        this.id = item.getId();
        this.name = item.getName();
        this.img = item.getImg();
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

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
