package com.thoth.server.model.domain.datasource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.thoth.server.controller.view.datasource.DatasourcePropertiesListItemView;
import com.thoth.server.controller.view.datasource.DatasourcePropertiesView;
import com.thoth.server.model.domain.Renderer;
import com.thoth.server.model.domain.security.SecuredResource;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.*;

@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class DatasourceProperties extends SecuredResource {
    @Id
    @NotBlank
    @Size(min = 3, max = 256)
    private String id;

    @NotBlank
    @Size(min = 3, max = 256)
    private String name;

    @NotBlank
    @Size(min = 3, max = 256)
    private String type;

    @Column(updatable = false)
    private Instant createdAt;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<@NotBlank @Size(min = 1, max = 256) String> parameters;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Property> properties;

    @ManyToMany(mappedBy = "datasourceProperties")
    @JsonIgnore
    private List<Renderer> usages;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        DatasourceProperties that = (DatasourceProperties) object;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


    public DatasourcePropertiesListItemView toListItemView(String uSid, String oSid){
        DatasourcePropertiesListItemView listItem = new DatasourcePropertiesListItemView();
        listItem.setId(this.id);
        listItem.setName(this.name);
        listItem.setType(this.type);
        listItem.setParameters(this.parameters);
        listItem.setProperties(this.properties);
        listItem.setPermission(checkPermission(uSid, oSid, false));
        return listItem;
    }

    public abstract DatasourcePropertiesView toView(String uSid, String oSid);
}
