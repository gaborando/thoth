package com.thoth.server.controller.dto.datasource;

import lombok.*;

import java.util.HashMap;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RestDatasourceParametersCheckRequest extends RestDatasourceParameters {
    private HashMap<String, String> parameters;
}
