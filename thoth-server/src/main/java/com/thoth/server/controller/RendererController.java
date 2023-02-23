package com.thoth.server.controller;

import com.thoth.server.controller.dto.renderer.RendererCreateRequest;
import com.thoth.server.controller.dto.PrintRequest;
import com.thoth.server.model.domain.Renderer;
import com.thoth.server.service.RenderService;
import com.thoth.server.service.RendererService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;

@RestController
@RequestMapping("/renderer")
public class RendererController {

    private final RendererService rendererService;

    private final RenderService renderService;

    public RendererController(RendererService rendererService, RenderService renderService) {
        this.rendererService = rendererService;
        this.renderService = renderService;
    }


    @Secured("ROLE_USER")
    @PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Renderer> create(@RequestBody RendererCreateRequest request) {
        return ResponseEntity.ok(rendererService.create(
                request.getName(),
                request.getTemplate(),
                request.getDatasource(),
                request.getAssociationMap()
        ));
    }


    @Secured("ROLE_USER")
    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<Renderer>> findAll(
            @RequestParam(defaultValue = "0") int page
    ) {
        return ResponseEntity.ok(rendererService.search(Specification.where(null),
                PageRequest.of(page, 10, Sort.by(Sort.Order.desc("createdAt")))));
    }


    @Secured("ROLE_USER")
    @GetMapping(value = "/{identifier}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Renderer> findById(
            @PathVariable String identifier) {
        return ResponseEntity.ok(rendererService.findById(identifier).orElseThrow());
    }


    @Secured("ROLE_USER")
    @PutMapping(value = "/{identifier}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Renderer> update(@RequestBody Renderer renderer, @PathVariable String identifier) {
        return ResponseEntity.ok(rendererService.update(identifier, renderer));
    }


    @Secured("ROLE_USER")
    @DeleteMapping("/{identifier}")
    public void delete(@PathVariable String identifier) {
        rendererService.delete(rendererService.findById(identifier).orElseThrow());
    }

    @Secured({"ROLE_USER", "ROLE_API"})
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/{identifier}/render/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> renderPdf(@RequestParam(required = false) HashMap<String, Object> p1,
                                            @RequestBody(required = false) HashMap<String, Object> p2,
                                            @PathVariable String identifier) throws Exception {

        var params = new HashMap<String, Object>();
        if (p1 != null) params.putAll(p1);
        if (p2 != null) params.putAll(p2);
        var data = renderService.renderRendererPdf(identifier, params);
        return ResponseEntity.ok(data);

    }

    @Secured({"ROLE_USER", "ROLE_API"})
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/{identifier}/render/jpeg", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> renderJpeg(@RequestParam(required = false) HashMap<String, Object> p1,
                                             @RequestBody(required = false) HashMap<String, Object> p2,
                                             @PathVariable String identifier) throws Exception {

        var params = new HashMap<String, Object>();
        if (p1 != null) params.putAll(p1);
        if (p2 != null) params.putAll(p2);
        var data = renderService.renderRendererJpeg(identifier, params);
        return ResponseEntity.ok(data);

    }

    @Secured({"ROLE_USER", "ROLE_API"})
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/{identifier}/render/svg", produces = "image/svg+xml")
    public ResponseEntity<byte[]> renderSvg(@RequestParam(required = false) HashMap<String, Object> p1,
                                            @RequestBody(required = false) HashMap<String, Object> p2,
                                            @PathVariable String identifier) throws Exception {

        var params = new HashMap<String, Object>();
        if (p1 != null) params.putAll(p1);
        if (p2 != null) params.putAll(p2);
        var data = renderService.renderRendererSvg(identifier, params);
        return ResponseEntity.ok(data.getBytes());

    }

    @Secured({"ROLE_USER", "ROLE_API"})
    @PostMapping("/{identifier}/print")
    public ResponseEntity<?> print(@RequestBody PrintRequest request, @PathVariable String identifier) throws Exception {
        renderService.printRenderer(identifier, request.getParameters(), request.getClientIdentifier(), request.getPrintService(), request.getCopies());
        return ResponseEntity.ok().build();
    }

}
