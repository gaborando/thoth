package com.thoth.server.controller.view;

import com.thoth.server.model.domain.security.Permission;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;
import java.util.Set;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class TemplateView extends SecuredResourceView {

    private String id;
    private String name;
    private String svg;
    private String img;
    private String xml;
    private Instant createdAt;
    private Set<String> markers;
    private String folder;
    private Permission permission;
    protected List<RendererListItemView> usages;
}