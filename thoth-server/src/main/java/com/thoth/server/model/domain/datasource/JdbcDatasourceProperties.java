package com.thoth.server.model.domain.datasource;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

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
}
