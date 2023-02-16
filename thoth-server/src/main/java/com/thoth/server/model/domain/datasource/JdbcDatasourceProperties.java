package com.thoth.server.model.domain.datasource;

import jakarta.persistence.Column;
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

    @Column(columnDefinition = "TEXT")
    private String query;
}
