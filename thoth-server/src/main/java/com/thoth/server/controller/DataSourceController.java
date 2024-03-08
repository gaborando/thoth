package com.thoth.server.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.thoth.server.beans.AuthenticationFacade;
import com.thoth.server.controller.dto.datasource.*;
import com.thoth.server.controller.view.datasource.DatasourcePropertiesListItemView;
import com.thoth.server.controller.view.datasource.DatasourcePropertiesView;
import com.thoth.server.controller.view.datasource.JdbcDatasourcePropertiesView;
import com.thoth.server.controller.view.datasource.RestDatasourcePropertiesView;
import com.thoth.server.model.domain.datasource.DatasourceProperties;
import com.thoth.server.model.domain.datasource.JdbcDatasourceProperties;
import com.thoth.server.model.domain.datasource.Property;
import com.thoth.server.model.domain.datasource.RestDatasourceProperties;
import com.thoth.server.service.DataSourceService;
import io.github.perplexhub.rsql.RSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.Set;

@RestController
@RequestMapping("/datasource")
@Secured("ROLE_USER")
public class DataSourceController {

    private final DataSourceService dataSourceService;

    public DataSourceController(DataSourceService dataSourceService) {
        this.dataSourceService = dataSourceService;
    }

    @PostMapping(value = "/check/jdbc", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Set<Property>> checkJdbcParameters(@RequestBody JdbcDatasourceParametersCheckRequest parameters) throws SQLException {
        return ResponseEntity.ok(dataSourceService.checkJdbc(
                parameters.getUrl(),
                parameters.getUsername(),
                parameters.getPassword(),
                parameters.getQuery(),
                parameters.getParameters()
        ));
    }

    @PostMapping(value = "/check/rest", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Set<Property>> checkRestParameters(@RequestBody RestDatasourceParametersCheckRequest parameters) throws SQLException, JsonProcessingException {
        return ResponseEntity.ok(dataSourceService.checkRest(
                parameters,
                parameters.getParameters()
        ));
    }


    @PostMapping(value = "/jdbc", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JdbcDatasourcePropertiesView> createJdbcParameters(
            @RequestBody JdbcDatasourceParametersCreateRequest parameters,
            AuthenticationFacade facade) throws SQLException {
        return ResponseEntity.ok(dataSourceService.createJdbc(
                parameters.getName(),
                parameters.getUrl(),
                parameters.getUsername(),
                parameters.getPassword(),
                parameters.getQuery(),
                parameters.getParameters(),
                parameters.getProperties()
        ).toView(facade.getUserSID(), facade.getOrganizationSID()));
    }

    @PostMapping(value = "/rest", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestDatasourcePropertiesView> createRestParameters(
            @RequestBody RestDatasourceParametersCreateRequest parameters,
            AuthenticationFacade facade) throws SQLException {
        return ResponseEntity.ok(dataSourceService.createRest(
                parameters.getName(),
                parameters.getUrl(),
                parameters.getMethod(),
                parameters.getQueryParameters(),
                parameters.getHeaders(),
                parameters.getJsonQuery(),
                parameters.getBody(),
                parameters.getParameters(),
                parameters.getProperties()
        ).toView(facade.getUserSID(),
                facade.getOrganizationSID()));
    }

    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({"ROLE_USER", "ROLE_API"})
    public ResponseEntity<Page<DatasourcePropertiesListItemView>> findAll(
            @RequestParam(value = "filter", defaultValue = "") String filter,
            @RequestParam(value = "sort", defaultValue = "createdAt,desc") String sort,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "30") int size,
            AuthenticationFacade facade

    ) {

        Specification<DatasourceProperties> f = RSQLJPASupport.toSpecification(filter);
        f = f.and(RSQLJPASupport.toSort(sort));
        return ResponseEntity.ok(dataSourceService.search(f,
                PageRequest.of(page, size)).map(e -> e.toListItemView(facade.getUserSID(), facade.getOrganizationSID())));
    }

    @GetMapping(value = "/{identifier}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DatasourcePropertiesView> findById(@PathVariable String identifier,
                                                             AuthenticationFacade facade) {
        return ResponseEntity.ok(dataSourceService.findById(identifier).map(e -> e.toView(facade.getUserSID(),
                facade.getOrganizationSID())).orElseThrow());
    }

    @PostMapping(value = "/jdbc/{identifier}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DatasourcePropertiesView> updateJdbcParameters(
            @RequestBody JdbcDatasourceProperties properties, @PathVariable String identifier,
            AuthenticationFacade facade) throws SQLException {
        return ResponseEntity.ok(dataSourceService.update(identifier, properties)
                .toView(facade.getUserSID(),
                        facade.getOrganizationSID()));
    }

    @PostMapping(value = "/rest/{identifier}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DatasourcePropertiesView> updateRestParameters(
            @RequestBody RestDatasourceProperties properties,
            @PathVariable String identifier,
            AuthenticationFacade facade) {
        return ResponseEntity.ok(dataSourceService.update(identifier, properties)
                .toView(facade.getUserSID(), facade.getOrganizationSID()));
    }

    @DeleteMapping("/{identifier}")
    public void deleteDatasource(@PathVariable String identifier) {
        var e = dataSourceService.findById(identifier).orElseThrow();
        dataSourceService.delete(e);
    }
}
