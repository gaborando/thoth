package com.thoth.server.model.domain.datasource;

import com.thoth.server.model.converters.JpaConverterJson;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
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
    @ElementCollection
    private Map<@NotEmpty @Size(max = 256) String, @NotEmpty @Size(max = 256) String> queryParameters;

    @Size(max = 100)
    @ElementCollection
    private Map<@NotEmpty @Size(max = 256) String, @NotEmpty @Size(max = 256) String> headers;

    @Column(columnDefinition = "JSON")
    @Convert(converter = JpaConverterJson.class)
    private Map<@NotEmpty @Size(max = 256) String, Object> body;


    @Size(max = 256)
    private String jsonQuery;

}
