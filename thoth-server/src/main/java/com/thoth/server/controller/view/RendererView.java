package com.thoth.server.controller.view;

import com.thoth.server.controller.view.datasource.DatasourcePropertiesListItemView;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class RendererView extends SecuredResourceView {

    private String id;

    private String name;

    private TemplateView template;

    private Set<DatasourcePropertiesListItemView> datasourceProperties;

    private Instant createdAt;

    private Map< String, AssociationView> associationMap;

    private Map< String, AssociationView> parametersMap;




}
