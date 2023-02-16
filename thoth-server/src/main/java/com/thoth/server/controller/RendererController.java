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

    @PostMapping("/")
    public ResponseEntity<Renderer> create(@RequestBody RendererCreateRequest request) {
        return ResponseEntity.ok(rendererService.create(
                request.getName(),
                request.getTemplate(),
                request.getDatasource(),
                request.getAssociationMap()
        ));
    }

    @GetMapping("/")
    public Page<Renderer> findAll(
            @RequestParam(defaultValue = "0") int page
    ) {
        return rendererService.search(Specification.where(null),
                PageRequest.of(page, 10));
    }

    @GetMapping("/{identifier}")
    public ResponseEntity<Renderer> findById(
            @PathVariable String identifier) {
        return ResponseEntity.ok(rendererService.findById(identifier).orElseThrow());
    }

    @PutMapping("/{identifier}")
    public Renderer update(@RequestBody Renderer renderer, @PathVariable String identifier) {
        var r = rendererService.findById(identifier).orElseThrow();
        r.setAssociationMap(renderer.getAssociationMap());
        r.setName(renderer.getName());
        return rendererService.save(r);
    }

    @DeleteMapping("/{identifier}")
    public void delete(@PathVariable String identifier) {
        rendererService.deleteById(identifier);
    }


    @GetMapping("/{identifier}/render/pdf")
    public ResponseEntity<byte[]> renderPdf(@RequestParam HashMap<String, Object> params,
                                            @PathVariable String identifier) throws IOException, InterruptedException {

        var data = renderService.renderRendererPdf(identifier, params);


        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE);
        return ResponseEntity.ok().headers(httpHeaders).body(data);

    }

    @GetMapping("/{identifier}/render/jpeg")
    public ResponseEntity<byte[]> renderJpeg(@RequestParam HashMap<String, Object> params,
                                             @PathVariable String identifier) throws IOException, InterruptedException {

        var data = renderService.renderRendererJpeg(identifier, params);


        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE);
        return ResponseEntity.ok().headers(httpHeaders).body(data);

    }

    @GetMapping("/{identifier}/render/svg")
    public ResponseEntity<byte[]> renderSvg(@RequestParam HashMap<String, Object> params,
                                            @PathVariable String identifier) throws IOException, InterruptedException {

        var data = renderService.renderRendererSvg(identifier, params);


        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, "image/svg+xml");
        return ResponseEntity.ok().headers(httpHeaders).body(data.getBytes());

    }

    @PostMapping("/{identifier}/print")
    public ResponseEntity<?> print(@RequestBody PrintRequest request, @PathVariable String identifier) throws IOException, InterruptedException {
        renderService.printRenderer(identifier, request.getParameters(), request.getClientIdentifier(), request.getPrintService());
        return ResponseEntity.ok().build();
    }

}
