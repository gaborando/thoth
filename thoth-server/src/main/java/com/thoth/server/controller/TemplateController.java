package com.thoth.server.controller;

import com.nimbusds.jose.shaded.gson.Gson;
import com.thoth.server.controller.dto.PrintRequest;
import com.thoth.server.controller.dto.template.CreateTemplateRequest;
import com.thoth.server.controller.dto.template.TemplateListItem;
import com.thoth.server.model.domain.Template;
import com.thoth.server.service.render.RenderService;
import com.thoth.server.service.TemplateService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.HashMap;

@RestController
@RequestMapping("/template")
public class TemplateController {

    private final TemplateService templateService;

    private final RenderService renderService;

    public TemplateController(TemplateService templateService, RenderService renderService) {
        this.templateService = templateService;
        this.renderService = renderService;
    }

    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({"ROLE_USER", "ROLE_API"})
    public ResponseEntity<Page<Template>> findAll(
            @RequestParam(defaultValue = "0") int page
    ) {
        return ResponseEntity.ok(templateService.search(Specification.where(null),
                PageRequest.of(page, 10, Sort.by(Sort.Order.desc("createdAt")))));
    }

    @GetMapping(value = "/{identifier}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({"ROLE_USER", "ROLE_API"})
    public ResponseEntity<Template> findById(
            @PathVariable String identifier) {
        return ResponseEntity.ok(templateService.getById(identifier).orElseThrow());
    }

    @PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({"ROLE_USER"})
    public ResponseEntity<Template> create(@RequestBody CreateTemplateRequest request) {
        return ResponseEntity.ok(templateService.create(request.getName()));
    }

    @PutMapping(value = "/{identifier}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({"ROLE_USER"})
    public ResponseEntity<Template> update(@RequestBody Template template, @PathVariable String identifier) throws ParserConfigurationException {
        return ResponseEntity.ok(templateService.update(identifier, template));
    }

    @DeleteMapping("/{identifier}")
    @Secured({"ROLE_USER"})
    public void delete(@PathVariable String identifier) {
        var e = templateService.getById(identifier).orElseThrow();
        templateService.delete(e);
    }

    @Secured({"ROLE_USER", "ROLE_API", "ROLE_TMP"})
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/{identifier}/render/jpeg", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> renderJpeg(@RequestParam(required = false) HashMap<String, Object> p1,
                                             @RequestBody(required = false) HashMap<String, Object> p2,
                                             @PathVariable String identifier) throws IOException, InterruptedException {

        var data = renderService.renderTemplateJpeg(identifier, parseParams(p1, p2));
        return ResponseEntity.ok(data);

    }

    @Secured({"ROLE_USER", "ROLE_API", "ROLE_TMP"})
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/{identifier}/render/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> renderPdf(@RequestParam(required = false) HashMap<String, Object> p1,
                                            @RequestBody(required = false) HashMap<String, Object> p2,
                                            @PathVariable String identifier) throws IOException, InterruptedException {

        var data = renderService.renderTemplatePdf(identifier, parseParams(p1, p2));
        return ResponseEntity.ok(data);

    }

    @Secured({"ROLE_USER", "ROLE_API", "ROLE_TMP"})
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST},
            value = "/{identifier}/render/svg", produces = "image/svg+xml")
    public ResponseEntity<byte[]> renderSvg(@RequestParam(required = false) HashMap<String, Object> p1,
                                            @RequestBody(required = false) HashMap<String, Object> p2,
                                            @PathVariable String identifier) throws IOException, InterruptedException {

        var data = renderService.renderTemplateSvg(identifier,  parseParams(p1, p2));
        return ResponseEntity.ok(data.getBytes());

    }

    private HashMap<String, Object> parseParams(@RequestParam(required = false) HashMap<String, Object> p1, @RequestBody(required = false) HashMap<String, Object> p2) {
        var params = new HashMap<String, Object>();
        if (p1 != null) params.putAll(p1);
        if (p2 != null) params.putAll(p2);
        if(params.containsKey("json")){
            var j = new Gson().fromJson(params.get("json").toString(), HashMap.class);
            for (Object s : j.keySet()) {
                params.put(s.toString(), j.get(s));
            }
        }
        return params;
    }

    @Secured({"ROLE_USER", "ROLE_API"})
    @PostMapping("/{identifier}/print")
    public ResponseEntity<?> print(@RequestBody PrintRequest request, @PathVariable String identifier) throws IOException, InterruptedException {

        renderService.printTemplate(identifier, request.getParameters(), request.getClientIdentifier(), request.getPrintService(), request.getCopies());

        return ResponseEntity.ok().build();
    }

}
