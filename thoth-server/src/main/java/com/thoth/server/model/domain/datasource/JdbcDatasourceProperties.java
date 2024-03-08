package com.thoth.server.model.domain.datasource;

import com.thoth.server.controller.view.datasource.DatasourcePropertiesView;
import com.thoth.server.controller.view.datasource.JdbcDatasourcePropertiesView;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import java.util.List;

@Entity
@Getter
@Setter
public class JdbcDatasourceProperties extends DatasourceProperties{

    @Size(min = 3, max = 256)
    private String url;

    @Size(max = 256)
    private String username;
    @Size(max = 256)
    private String password;

    @Column(columnDefinition = "TEXT")
    private String query;

    @Override
    public JdbcDatasourcePropertiesView toView(String uSid, String oSid) {
        JdbcDatasourcePropertiesView view = new JdbcDatasourcePropertiesView();
        view.setId(this.getId());
        view.setName(this.getName());
        view.setType(this.getType());
        view.setParameters(this.getParameters());
        view.setProperties(this.getProperties());
        view.setCreatedAt(getCreatedAt());
        view.setUsages(getUsages() == null ? List.of() : getUsages().stream().map(u -> u.toListItemView(uSid, oSid)).toList());
        setView(view, uSid, oSid);
        view.setUrl(url);
        view.setUsername(username);
        view.setPassword(password);
        view.setQuery(query);
        return view;
    }
}
