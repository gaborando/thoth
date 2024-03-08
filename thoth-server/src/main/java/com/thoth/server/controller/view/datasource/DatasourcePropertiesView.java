package com.thoth.server.controller.view.datasource;

import com.thoth.server.controller.view.RendererListItemView;
import com.thoth.server.controller.view.SecuredResourceView;
import com.thoth.server.model.domain.Renderer;
import com.thoth.server.model.domain.datasource.Property;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public abstract class DatasourcePropertiesView extends SecuredResourceView {
    private String id;

    private String name;

    private String type;

    private Instant createdAt;

    private Set<String> parameters;

    private Set<Property> properties;

    private List<RendererListItemView> usages;
}
