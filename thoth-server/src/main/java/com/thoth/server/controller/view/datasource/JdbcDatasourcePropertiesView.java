package com.thoth.server.controller.view.datasource;

import com.thoth.server.model.domain.datasource.DatasourceProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode(callSuper = true)
@Data
public class JdbcDatasourcePropertiesView extends DatasourcePropertiesView {

    private String url;
    private String username;
    private String password;
    private String query;
}
