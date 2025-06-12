package com.thoth.server.model.domain.datasource;

import com.thoth.server.controller.view.datasource.RestDatasourcePropertiesView;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter
public class RestDatasourceProperties extends DatasourceProperties {

    @NotEmpty
    @Size(max = 256)
    private String url;
    @NotEmpty
    @Size(max = 10)
    private String method;


    @Size(max = 100)
    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<@NotEmpty @Size(max = 256) String, @NotEmpty @Size(max = 256) String> queryParameters;

    @Size(max = 100)
    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<@NotEmpty @Size(max = 256) String, @NotEmpty @Size(max = 256) String> headers;

    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<@NotEmpty @Size(max = 256) String, Object> body;


    @Size(max = 256)
    private String jsonQuery;

    @Override
    public RestDatasourcePropertiesView toView(String uSid, String oSid) {
        RestDatasourcePropertiesView view = new RestDatasourcePropertiesView();
        view.setId(this.getId());
        view.setName(this.getName());
        view.setType(this.getType());
        view.setParameters(this.getParameters());
        view.setProperties(this.getProperties());
        view.setCreatedAt(getCreatedAt());
        view.setUsages(getUsages() == null ? List.of() : getUsages().stream().map(u -> u.toListItemView(uSid, oSid)).toList());
        setView(view, uSid, oSid);
        view.setUrl(url);
        view.setMethod(method);
        view.setQueryParameters(queryParameters);
        view.setHeaders(headers);
        view.setBody(body);
        view.setJsonQuery(jsonQuery);
        return view;
    }
}
