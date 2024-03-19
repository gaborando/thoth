package com.thoth.server.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.thoth.server.controller.view.TemplateListItemView;
import com.thoth.server.controller.view.TemplateView;
import com.thoth.server.model.domain.security.SecuredResource;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;
import java.util.List;

@Entity
@Getter
@Setter
public class Template extends SecuredResource {
    @Id
    @Column(updatable = false)
    @NotBlank
    @Size(min = 3, max = 256)
    private String id;

    @NotBlank
    @Size(min = 3, max = 256)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String svg;

    @Column(columnDefinition = "TEXT")
    private String img;

    @Column(columnDefinition = "TEXT")
    private String xml;

    @Column(updatable = false)
    private Instant createdAt;

    @ElementCollection
    private Set<@NotBlank @Size(min = 2, max = 256) String> markers;

    @NotBlank
    @Size(min = 1, max = 256)
    private String folder;

    @OneToMany(mappedBy = "template")
    @JsonIgnore
    private List<Renderer> usages;

    public TemplateView toView(String uSid, String oSid){
        TemplateView view = new TemplateView();
        view.setId(id);
        view.setName(name);
        view.setImg(img);
        view.setMarkers(markers);
        view.setFolder(folder);
        view.setSvg(svg);
        view.setXml(xml);
        view.setCreatedAt(createdAt);
        setView(view, uSid, oSid);
        view.setUsages(usages == null ? List.of() : usages.stream().map(u -> u.toListItemView(uSid, oSid)).toList());
        return view;
    }

    public TemplateListItemView toListItemView(String uSid, String oSid){
        TemplateListItemView view = new TemplateListItemView();
        view.setId(id);
        view.setName(name);
        view.setImg(img);
        view.setMarkers(markers);
        view.setPermission(checkPermission(uSid, oSid));
        return view;
    }
}
