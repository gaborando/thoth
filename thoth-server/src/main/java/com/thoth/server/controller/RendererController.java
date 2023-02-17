package com.thoth.server.controller;

import com.thoth.server.controller.dto.renderer.RendererCreateRequest;
import com.thoth.server.controller.dto.PrintRequest;
import com.thoth.server.model.domain.Renderer;
import com.thoth.server.service.RenderService;
import com.thoth.server.service.RendererService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    @PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Renderer> create(@RequestBody RendererCreateRequest request) {
        return ResponseEntity.ok(rendererService.create(
                request.getName(),
                request.getTemplate(),
                request.getDatasource(),
                request.getAssociationMap()
        ));
    }

    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<Renderer>> findAll(
            @RequestParam(defaultValue = "0") int page
    ) {
        return ResponseEntity.ok(rendererService.search(Specification.where(null),
                PageRequest.of(page, 10)));
    }

    @GetMapping(value = "/{identifier}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Renderer> findById(
            @PathVariable String identifier) {
        return ResponseEntity.ok(rendererService.findById(identifier).orElseThrow());
    }

    @PutMapping(value = "/{identifier}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Renderer> update(@RequestBody Renderer renderer, @PathVariable String identifier) {
        var r = rendererService.findById(identifier).orElseThrow();
        r.setAssociationMap(renderer.getAssociationMap());
        r.setName(renderer.getName());
        return ResponseEntity.ok(rendererService.save(r));
    }

    @DeleteMapping("/{identifier}")
    public void delete(@PathVariable String identifier) {
        rendererService.deleteById(identifier);
    }


    @GetMapping(value = "/{identifier}/render/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> renderPdf(@RequestParam HashMap<String, Object> params,
                                            @PathVariable String identifier) throws Exception {

        var data = renderService.renderRendererPdf(identifier, params);

        return ResponseEntity.ok(data);

    }

    @GetMapping(value = "/{identifier}/render/jpeg", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> renderJpeg(@RequestParam HashMap<String, Object> params,
                                             @PathVariable String identifier) throws Exception {

        var data = renderService.renderRendererJpeg(identifier, params);
        return ResponseEntity.ok(data);

    }

    @GetMapping(value = "/{identifier}/render/svg", produces = "image/svg+xml")
    public ResponseEntity<byte[]> renderSvg(@RequestParam HashMap<String, Object> params,
                                            @PathVariable String identifier) throws Exception {

        var data = renderService.renderRendererSvg(identifier, params);
        return ResponseEntity.ok(data.getBytes());

    }

    @PostMapping("/{identifier}/print")
    public ResponseEntity<?> print(@RequestBody PrintRequest request, @PathVariable String identifier) throws Exception {
        renderService.printRenderer(identifier, request.getParameters(), request.getClientIdentifier(), request.getPrintService());
        return ResponseEntity.ok().build();
    }

}
