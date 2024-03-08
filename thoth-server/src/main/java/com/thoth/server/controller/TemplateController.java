package com.thoth.server.controller;

import com.nimbusds.jose.shaded.gson.Gson;
import com.thoth.server.beans.AuthenticationFacade;
import com.thoth.server.controller.dto.PrintRequest;
import com.thoth.server.controller.dto.RenderRequest;
import com.thoth.server.controller.dto.template.CreateTemplateRequest;
import com.thoth.server.controller.view.TemplateListItemView;
import com.thoth.server.controller.view.TemplateView;
import com.thoth.server.model.domain.Template;
import com.thoth.server.service.TemplateService;
import com.thoth.server.service.render.RenderService;
import io.github.perplexhub.rsql.RSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

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
    public ResponseEntity<Page<TemplateListItemView>> findAll(
            @RequestParam(value = "filter", defaultValue = "") String filter,
            @RequestParam(value = "sort", defaultValue = "createdAt,desc") String sort,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "15") int size,
            AuthenticationFacade facade

    ) {

        Specification<Template> f = RSQLJPASupport.toSpecification(filter);
        f = f.and(RSQLJPASupport.toSort(sort));
        return ResponseEntity.ok(templateService.search(f
                        , PageRequest.of(page, size))
                .map(e -> e.toListItemView(facade.getUserSID(), facade.getOrganizationSID())));
    }

    @GetMapping(value = "/folders", produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({"ROLE_USER"})
    public ResponseEntity<List<String>> findAllFolders() {
        return ResponseEntity.ok(templateService.getFolders());
    }

    @GetMapping(value = "/{identifier}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({"ROLE_USER", "ROLE_API"})
    public ResponseEntity<TemplateView> findById(
            @PathVariable String identifier,
            AuthenticationFacade facade) {
        return ResponseEntity.ok(templateService.getById(identifier)
                .map(e -> e.toView(facade.getUserSID(), facade.getOrganizationSID())).orElseThrow());
    }

    @PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({"ROLE_USER"})
    public ResponseEntity<TemplateView> create(@RequestBody CreateTemplateRequest request,
                                               AuthenticationFacade facade) {
        return ResponseEntity.ok(templateService.create(request.getName()).toView(facade.getUserSID(),
                facade.getOrganizationSID()));
    }

    @PutMapping(value = "/{identifier}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({"ROLE_USER"})
    public ResponseEntity<TemplateView> update(@RequestBody Template template,
                                               @PathVariable String identifier,
                                               AuthenticationFacade facade) throws ParserConfigurationException {
        return ResponseEntity.ok(templateService.update(
                templateService.getById(identifier).orElseThrow(), template)
                .toView(facade.getUserSID(), facade.getOrganizationSID()));
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
    @CrossOrigin(allowedHeaders = "*", origins = "*")
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/{identifier}/render/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> renderPdf(@RequestParam(required = false) HashMap<String, Object> p1,
                                            @RequestBody(required = false) HashMap<String, Object> p2,
                                            @PathVariable String identifier) throws IOException, InterruptedException {

        var data = renderService.renderTemplatePdf(identifier, parseParams(p1, p2));
        return ResponseEntity.ok(data);

    }

    @Secured({"ROLE_USER", "ROLE_API", "ROLE_TMP"})
    @PostMapping(value = "/render/pdf-multi", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> renderMultiPdf(@RequestBody List<RenderRequest> requests) throws Exception {
        var data = renderService.renderMultiTemplatePdf(requests);
        return ResponseEntity.ok(data);
    }

    @Secured({"ROLE_USER", "ROLE_API", "ROLE_TMP"})
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST},
            value = "/{identifier}/render/svg", produces = "image/svg+xml")
    public ResponseEntity<byte[]> renderSvg(@RequestParam(required = false) HashMap<String, Object> p1,
                                            @RequestBody(required = false) HashMap<String, Object> p2,
                                            @PathVariable String identifier) throws IOException, InterruptedException {

        var data = renderService.renderTemplateSvg(identifier, parseParams(p1, p2));
        return ResponseEntity.ok(data.getBytes(StandardCharsets.UTF_8));

    }

    private HashMap<String, Object> parseParams(
            @RequestParam(required = false) HashMap<String, Object> p1,
            @RequestBody(required = false) HashMap<String, Object> p2) {
        var params = new HashMap<String, Object>();
        if (p1 != null) params.putAll(p1);
        if (p2 != null) params.putAll(p2);
        if (params.containsKey("json")) {
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
        renderService.printTemplate(
                identifier,
                request.getParameters(),
                request.getClientIdentifier(),
                request.getPrintService(),
                request.getCopies());
        return ResponseEntity.ok().build();
    }

}
