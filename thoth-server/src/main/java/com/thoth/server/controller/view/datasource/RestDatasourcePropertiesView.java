package com.thoth.server.controller.view.datasource;

import com.thoth.server.model.converters.JpaConverterJson;
import com.thoth.server.model.domain.datasource.DatasourceProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class RestDatasourcePropertiesView extends DatasourcePropertiesView {


    private String url;

    private String method;

    private Map<String, String> queryParameters;

    private Map<String, String> headers;

    private Map<String, Object> body;

    private String jsonQuery;

}
