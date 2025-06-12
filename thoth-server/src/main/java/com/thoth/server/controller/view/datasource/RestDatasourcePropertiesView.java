package com.thoth.server.controller.view.datasource;

import lombok.Data;
import lombok.EqualsAndHashCode;

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
