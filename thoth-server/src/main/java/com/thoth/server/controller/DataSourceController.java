package com.thoth.server.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.thoth.server.controller.dto.datasource.*;
import com.thoth.server.model.domain.Template;
import com.thoth.server.model.domain.datasource.DatasourceProperties;
import com.thoth.server.model.domain.datasource.JdbcDatasourceProperties;
import com.thoth.server.model.domain.datasource.RestDatasourceProperties;
import com.thoth.server.service.DataSourceService;
import io.github.perplexhub.rsql.RSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.ArrayList;

@RestController
@RequestMapping("/datasource")
@Secured("ROLE_USER")
public class DataSourceController {

    private final DataSourceService dataSourceService;

    public DataSourceController(DataSourceService dataSourceService) {
        this.dataSourceService = dataSourceService;
    }

    @PostMapping(value = "/check/jdbc", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String[]> checkJdbcParameters(@RequestBody JdbcDatasourceParametersCheckRequest parameters) throws SQLException {
        return ResponseEntity.ok(dataSourceService.checkJdbc(
                parameters.getUrl(),
                parameters.getUsername(),
                parameters.getPassword(),
                parameters.getQuery(),
                parameters.getParameters()
        ));
    }

    @PostMapping(value = "/check/rest", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ArrayList<String>> checkRestParameters(@RequestBody RestDatasourceParametersCheckRequest parameters) throws SQLException, JsonProcessingException {
        return ResponseEntity.ok(dataSourceService.checkRest(
                parameters.getRequest(),
                parameters.getParameters()
        ));
    }


    @PostMapping(value = "/jdbc", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DatasourceProperties> createJdbcParameters(@RequestBody JdbcDatasourceParametersCreateRequest parameters) throws SQLException {
        return ResponseEntity.ok(dataSourceService.createJdbc(
                parameters.getName(),
                parameters.getUrl(),
                parameters.getUsername(),
                parameters.getPassword(),
                parameters.getQuery(),
                parameters.getParameters(),
                parameters.getProperties()
        ));
    }

    @PostMapping(value = "/rest", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DatasourceProperties> createJdbcParameters(@RequestBody RestDatasourceParametersCreateRequest parameters) throws SQLException {
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
        ));
    }

    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({"ROLE_USER", "ROLE_API"})
    public ResponseEntity<Page<DatasourcePropertyListItem>> findAll(
            @RequestParam(value = "filter", defaultValue = "") String filter,
            @RequestParam(value = "sort", defaultValue = "createdAt,desc") String sort,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "30") int size

    ) {

        Specification<DatasourceProperties> f = RSQLJPASupport.toSpecification(filter);
        f = f.and(RSQLJPASupport.toSort(sort));
        return ResponseEntity.ok(dataSourceService.search(f,
                PageRequest.of(page, size)).map(DatasourcePropertyListItem::new));
    }

    @GetMapping(value = "/{identifier}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DatasourceProperties> findById(@PathVariable String identifier) {
        return ResponseEntity.ok(dataSourceService.findById(identifier).orElseThrow());
    }

    @PostMapping(value = "/jdbc/{identifier}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DatasourceProperties> updateJdbcParameters(@RequestBody JdbcDatasourceProperties properties, @PathVariable String identifier) throws SQLException {
        return ResponseEntity.ok(dataSourceService.update(identifier, properties));
    }

    @PostMapping(value = "/rest/{identifier}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DatasourceProperties> updateRestParameters(@RequestBody RestDatasourceProperties properties, @PathVariable String identifier) throws SQLException {
        return ResponseEntity.ok(dataSourceService.update(identifier, properties));
    }

    @DeleteMapping("/{identifier}")
    public void deleteJdbcParameters(@PathVariable String identifier) {
        var e = dataSourceService.findById(identifier).orElseThrow();
        dataSourceService.delete(e);
    }
}
