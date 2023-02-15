package com.thot.server.model.domain.datasource;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class JdbcDatasourceProperties extends DatasourceProperties{
    private String url;
    private String username;
    private String password;
    private String query;
}
