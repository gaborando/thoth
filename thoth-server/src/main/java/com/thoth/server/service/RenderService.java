package com.thoth.server.service;

import com.thoth.server.controller.dto.renderer.Association;
import com.thoth.server.model.domain.Renderer;
import com.thoth.server.model.domain.Template;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thoth.common.Jpeg2Pdf;
import org.thoth.common.Svg2Jpeg;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class RenderService {

    private final TemplateService templateService;

    private final RendererService rendererService;

    private final DataSourceService dataSourceService;
    private final Svg2Jpeg svg2Jpeg;

    private final ClientService clientService;

    public RenderService(
            TemplateService templateService,
            RendererService rendererService,
            DataSourceService dataSourceService,
            ClientService clientService) {
        this.templateService = templateService;
        this.rendererService = rendererService;
        this.dataSourceService = dataSourceService;
        this.clientService = clientService;
        svg2Jpeg = new Svg2Jpeg();
    }

    public String renderTemplateSvg(String identifier, HashMap<String, Object> params) throws IOException, InterruptedException {

        var template = templateService.getById(identifier).orElseThrow();
        return renderTemplateSvg(template, params);
    }

    public String renderTemplateSvg(Template template, HashMap<String, Object> params) throws IOException, InterruptedException {

        var svg = template.getSvg();
        // Image Embedding Correction
        svg = svg.replace("https://embed.diagrams.net/{{", "{{");
        for (Map.Entry<String, Object> e : params.entrySet()) {
            svg = svg.replace("{{" + e.getKey() + "}}", e.getValue() == null ? "" : e.getValue().toString());
        }
        return svg;
    }

    private byte[] renderTemplateJpeg(Template template, HashMap<String, Object> params) throws IOException, InterruptedException {
        return svg2Jpeg.convert(renderTemplateSvg(template, params));
    }

    public byte[] renderTemplateJpeg(String identifier, HashMap<String, Object> params) throws IOException, InterruptedException {
        var template = templateService.getById(identifier).orElseThrow();
        return renderTemplateJpeg(template, params);


    }

    public byte[] renderTemplatePdf(String identifier, HashMap<String, Object> params) throws IOException, InterruptedException {
        var template = templateService.getById(identifier).orElseThrow();
        return renderTemplatePdf(template, params);
    }

    public byte[] renderTemplatePdf(Template template, HashMap<String, Object> params) throws IOException, InterruptedException {
        var img = renderTemplateJpeg(template, params);
        return Jpeg2Pdf.convert(img);
    }

    public byte[] renderRendererPdf(String identifier, HashMap<String, Object> params) throws Exception {
        var img = renderRendererJpeg(identifier, params);
        return Jpeg2Pdf.convert(img);
    }


    public String renderRendererSvg(String identifier, HashMap<String, Object> params) throws Exception {
        var renderer = rendererService.findById(identifier).orElseThrow();
        return renderRendererSvg(renderer, params);
    }

    public String renderRendererSvg(Renderer renderer, HashMap<String, Object> params) throws Exception {
        var allParams = new HashMap<String, Object>();
        for (String marker : renderer.getTemplate().getMarkers()) {
            allParams.put(marker, "");
        }

        var dsDataMap = new HashMap<String, HashMap<String, Object>>();
        for (Map.Entry<String, Association> e : renderer.getAssociationMap().entrySet()) {

            if (e.getValue().getType().equals("datasource")) {
                if (!dsDataMap.containsKey(e.getValue().getId())) {
                    dsDataMap.put(e.getValue().getId(), dataSourceService.fetchData(e.getValue().getId(), params));
                }
                var dsData = dsDataMap.get(e.getValue().getId());

                allParams.put(e.getKey(), dsData.getOrDefault(e.getValue().getProperty(), ""));
            } else {
                allParams.put(e.getKey(), params.getOrDefault(e.getKey(), ""));
            }

        }
        return renderTemplateSvg(renderer.getTemplate(), allParams);
    }

    public byte[] renderRendererJpeg(String identifier, HashMap<String, Object> params) throws Exception {
        var renderer = rendererService.findById(identifier).orElseThrow();
        return renderRendererJpeg(renderer, params);
    }

    private byte[] renderRendererJpeg(Renderer renderer, HashMap<String, Object> params) throws Exception {


        var allParams = new HashMap<String, Object>();
        for (String marker : renderer.getTemplate().getMarkers()) {
            allParams.put(marker, "");
        }

        var dsDataMap = new HashMap<String, HashMap<String, Object>>();
        for (Map.Entry<String, Association> e : renderer.getAssociationMap().entrySet()) {

            if (e.getValue().getType().equals("datasource")) {
                if (!dsDataMap.containsKey(e.getValue().getId())) {
                    dsDataMap.put(e.getValue().getId(), dataSourceService.fetchData(e.getValue().getId(), params));
                }
                var dsData = dsDataMap.get(e.getValue().getId());

                allParams.put(e.getKey(), dsData.getOrDefault(e.getValue().getProperty(), ""));
            } else {
                allParams.put(e.getKey(), params.getOrDefault(e.getKey(), ""));
            }

        }

        return renderTemplateJpeg(renderer.getTemplate(), allParams);
    }

    public void printRenderer(String identifier, HashMap<String, Object> parameters, String clientIdentifier, String printService) throws Exception {
        clientService.printSvg(clientIdentifier, printService, renderRendererSvg(identifier, parameters));
    }

    public void printTemplate(String identifier, HashMap<String, Object> parameters, String clientIdentifier, String printService) throws IOException, InterruptedException {
        clientService.printSvg(clientIdentifier, printService, renderTemplateSvg(identifier, parameters));
    }
}
