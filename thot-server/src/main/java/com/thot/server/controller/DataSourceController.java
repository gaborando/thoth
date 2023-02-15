package com.thot.server.controller;

import com.thot.server.controller.dto.datasource.JdbcDatasourceParametersCheckRequest;
import com.thot.server.controller.dto.datasource.JdbcDatasourceParametersCreateRequest;
import com.thot.server.model.domain.datasource.DatasourceProperties;
import com.thot.server.model.domain.datasource.JdbcDatasourceProperties;
import com.thot.server.service.DataSourceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
@RequestMapping("/datasource")
public class DataSourceController {

    private final DataSourceService dataSourceService;

    public DataSourceController(DataSourceService dataSourceService) {
        this.dataSourceService = dataSourceService;
    }

    @PostMapping("/check/jdbc")
    public ResponseEntity<String[]> checkJdbcParameters(@RequestBody JdbcDatasourceParametersCheckRequest parameters) throws SQLException {
        return ResponseEntity.ok(dataSourceService.checkJdbc(
                parameters.getUrl(),
                parameters.getUsername(),
                parameters.getPassword(),
                parameters.getQuery(),
                parameters.getParameters()
        ));
    }

    @PostMapping("/jdbc")
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

    @GetMapping("/")
    public Page<DatasourceProperties> findAll(
            @RequestParam(defaultValue = "0") int page
    ) {
        return dataSourceService.search(Specification.where(null),
                PageRequest.of(page, 10));
    }

    @GetMapping("/{identifier}")
    public ResponseEntity<DatasourceProperties> findById(@PathVariable String identifier) {
        return ResponseEntity.ok(dataSourceService.findById(identifier).orElseThrow());
    }

    @PostMapping("/jdbc/{identifier}")
    public ResponseEntity<DatasourceProperties> updateJdbcParameters(@RequestBody JdbcDatasourceProperties properties, @PathVariable String identifier) throws SQLException {
        properties.setId(identifier);
        return ResponseEntity.ok(dataSourceService.update(properties));
    }

    @DeleteMapping("/{identifier}")
    public void updateJdbcParameters(@PathVariable String identifier) throws SQLException {
        dataSourceService.delete(identifier);
    }
}
