package com.thoth.server.controller;

import com.thoth.server.controller.dto.PrintRequest;
import com.thoth.server.controller.dto.template.CreateTemplateRequest;
import com.thoth.server.model.domain.Template;
import com.thoth.server.service.RenderService;
import com.thoth.server.service.TemplateService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.HashMap;

@RestController
@RequestMapping("/template")
// @CrossOrigin(origins = "*", allowedHeaders = "*")
public class TemplateController {



    private final TemplateService templateService;

    private final RenderService renderService;

    public TemplateController(TemplateService templateService, RenderService renderService) {
        this.templateService = templateService;
        this.renderService = renderService;
    }

    @GetMapping("/")
    public Page<Template> findAll(
            @RequestParam(defaultValue = "0") int page
    ) {
        return templateService.search(Specification.where(null),
                PageRequest.of(page, 10));
    }

    @PostMapping("/")
    public Template create(@RequestBody CreateTemplateRequest request) {
        return templateService.create(request.getName());
    }

    @PutMapping("/{identifier}")
    public Template update(@RequestBody Template template, @PathVariable String identifier) {
        template.setId(identifier);
        return templateService.save(template);
    }

    @DeleteMapping("/{identifier}")
    public void delete(@PathVariable String identifier) {
        templateService.deleteById(identifier);
    }

    @GetMapping("/{identifier}/render/jpeg")
    public ResponseEntity<byte[]> renderJpeg(@RequestParam HashMap<String, Object> params,
                                         @PathVariable String identifier) throws IOException, InterruptedException {

        var data = renderService.renderTemplateJpeg(identifier, params);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE);
        return ResponseEntity.ok().headers(httpHeaders).body(data);

    }

    @GetMapping("/{identifier}/render/pdf")
    public ResponseEntity<byte[]> renderPdf(@RequestParam HashMap<String, Object> params,
                                             @PathVariable String identifier) throws IOException, InterruptedException {

        var data = renderService.renderTemplatePdf(identifier, params);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE);
        return ResponseEntity.ok().headers(httpHeaders).body(data);

    }

    @GetMapping("/{identifier}/render/svg")
    public ResponseEntity<byte[]> renderSvg(@RequestParam HashMap<String, Object> params,
                                            @PathVariable String identifier) throws IOException, InterruptedException {

        var data = renderService.renderTemplateSvg(identifier, params);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, "image/svg+xml");
        return ResponseEntity.ok().headers(httpHeaders).body(data.getBytes());

    }

    @PostMapping("/{identifier}/print")
    public ResponseEntity<?> print(@RequestBody PrintRequest request, @PathVariable String identifier) throws IOException, InterruptedException {
        renderService.printTemplate(identifier, request.getParameters(), request.getClientIdentifier(), request.getPrintService());

        return ResponseEntity.ok().build();
    }

}
