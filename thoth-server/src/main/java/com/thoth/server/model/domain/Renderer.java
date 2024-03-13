package com.thoth.server.model.domain;

import com.thoth.server.controller.view.AssociationView;
import com.thoth.server.controller.view.RendererListItemView;
import com.thoth.server.controller.view.RendererView;
import com.thoth.server.model.domain.datasource.DatasourceProperties;
import com.thoth.server.model.domain.security.SecuredResource;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
public class Renderer extends SecuredResource {

    @Id
    @NotBlank
    @Size(min = 3, max = 256)
    private String id;

    @NotBlank
    @Size(min = 3, max = 256)
    private String name;

    @ManyToOne
    @NotNull
    private Template template;

    @ManyToMany
    @Column(insertable = false, updatable = false)
    private Set<DatasourceProperties> datasourceProperties;

    @Column(updatable = false)
    private Instant createdAt;

    @ElementCollection
    private Map<@NotBlank @Size(min = 1, max = 256) String, @NotNull AssociationView> associationMap;

    @ElementCollection
    private Map<@NotBlank @Size(min = 1, max = 256) String, @NotNull AssociationView> parametersMap;

    public RendererListItemView toListItemView(String uSid, String oSid){
        RendererListItemView view = new RendererListItemView();
        view.setId(this.id);
        view.setName(this.name);
        view.setPermission(checkPermission(uSid, oSid));
        return view;
    }

    public RendererView toView(String uSid, String oSid){
        RendererView view = new RendererView();
        view.setId(this.id);
        view.setName(this.name);
        view.setTemplate(this.template.toView(uSid, oSid));
        view.setDatasourceProperties(this.datasourceProperties.stream()
                .map(d -> d.toListItemView(uSid, oSid))
                .collect(Collectors.toSet()));
        view.setCreatedAt(this.createdAt);
        view.setAssociationMap(this.associationMap);
        view.setParametersMap(this.parametersMap);
        setView(view, uSid, oSid);
        return view;
    }


}
