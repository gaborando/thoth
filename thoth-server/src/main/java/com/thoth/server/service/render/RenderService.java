package com.thoth.server.service.render;

import com.hubspot.jinjava.Jinjava;
import com.thoth.server.configuration.security.SecuredTimestampService;
import com.thoth.server.controller.dto.RenderRequest;
import com.thoth.server.controller.dto.renderer.Association;
import com.thoth.server.model.domain.Renderer;
import com.thoth.server.model.domain.Template;
import com.thoth.server.service.ClientService;
import com.thoth.server.service.DataSourceService;
import com.thoth.server.service.RendererService;
import com.thoth.server.service.TemplateService;
import com.thoth.server.service.render.pipes.DatePipe;
import com.thoth.server.service.render.pipes.NumberPipe;
import com.thoth.server.service.render.pipes.PaddingPipe;
import com.thoth.server.service.render.pipes.TrimPipe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thoth.common.Jpeg2Pdf;
import org.thoth.common.PdfMerger;
import org.thoth.common.Svg2Jpeg;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RenderService {

    private final TemplateService templateService;

    private final RendererService rendererService;

    private final DataSourceService dataSourceService;

    private final ClientService clientService;

    private final SecuredTimestampService securedTimestampService;
    private final Jinjava jinjava;

    @Value("${thoth.render.delay}")
    private long renderDelay;

    public RenderService(
            TemplateService templateService,
            RendererService rendererService,
            DataSourceService dataSourceService,
            ClientService clientService, SecuredTimestampService securedTimestampService) {
        this.templateService = templateService;
        this.rendererService = rendererService;
        this.dataSourceService = dataSourceService;
        this.clientService = clientService;
        this.securedTimestampService = securedTimestampService;
        jinjava = new Jinjava();
        jinjava.getGlobalContext().registerFilter(new PaddingPipe());
        jinjava.getGlobalContext().registerFilter(new NumberPipe());
        jinjava.getGlobalContext().registerFilter(new DatePipe());
        jinjava.getGlobalContext().registerFilter(new TrimPipe());
    }

    public String renderTemplateSvg(String identifier, HashMap<String, Object> params) throws IOException, InterruptedException {

        var template = templateService.getById(identifier).orElseThrow();
        return renderTemplateSvg(template, params);
    }

    public String renderTemplateSvg(Template template, HashMap<String, Object> params) throws IOException, InterruptedException {

        var allParams = new  HashMap<String, Object>();
        for (String marker : template.getMarkers()) {
            allParams.put(marker, "");
        }
        allParams.putAll(params);
        var tmpKey = securedTimestampService.generate();
        var svg = template.getSvg();
        // Image Embedding Correction
        svg = svg.replace("https://embed.diagrams.net/{{", "{{");
        svg = svg.replace("utils/barcode?", "utils/barcode?TMP_KEY=" + tmpKey + "&amp;");
        svg = svg.replace("utils/qrcode?", "utils/qrcode?TMP_KEY=" + tmpKey + "&amp;");

        return jinjava.render(svg, allParams);
    }

    private byte[] renderTemplateJpeg(Template template, HashMap<String, Object> params) throws IOException, InterruptedException {
        return Svg2Jpeg.convert(renderTemplateSvg(template, params), renderDelay);
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

    public byte[] renderMultiTemplatePdf(List<RenderRequest> requests) throws Exception {
        return PdfMerger.merge(requests.stream().map(r -> {
            try {
                return renderTemplateJpeg(r.getIdentifier(), r.getParameters());
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }).filter(Objects::nonNull));
    }

    public byte[] renderRendererPdf(String identifier, HashMap<String, Object> params) throws Exception {
        var img = renderRendererJpeg(identifier, params);
        return Jpeg2Pdf.convert(img);
    }

    public byte[] renderMultiRendererPdf(List<RenderRequest> requests) throws Exception {
        return PdfMerger.merge(requests.stream().map(r -> {
            try {
                return renderRendererJpeg(r.getIdentifier(), r.getParameters());
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }).filter(Objects::nonNull));
    }


    public String renderRendererSvg(String identifier, HashMap<String, Object> params) throws Exception {
        var renderer = rendererService.findById(identifier).orElseThrow();
        return renderRendererSvg(renderer, params);
    }

    public String renderRendererSvg(Renderer renderer, HashMap<String, Object> params) throws Exception {
        return renderTemplateSvg(renderer.getTemplate(), generateParams(renderer, params));
    }

    private HashMap<String, Object> generateParams(Renderer renderer, HashMap<String, Object> params) throws Exception {
        var allParams = new HashMap<String, Object>();
        for (String marker : renderer.getTemplate().getMarkers()) {
            allParams.put(marker, "");
        }
        var dsDataMap = new HashMap<String, HashMap<String, Object>>();
        var q = new LinkedList<>(renderer.getParametersMap().entrySet().stream().sorted(Map.Entry.comparingByKey()).toList());
        var err = 0;
        while (!q.isEmpty()){
            var e = q.remove();
            try {
                if (e.getValue().getType().equals("datasource")) {
                    if (!dsDataMap.containsKey(e.getValue().getId())) {
                        dsDataMap.put(e.getValue().getId(), dataSourceService.fetchData(e.getValue().getId(), params));
                    }
                    var dsData = dsDataMap.get(e.getValue().getId());
                    params.put(e.getKey(), dsData.getOrDefault(e.getValue().getProperty(), ""));
                }
                err = 0;
            }catch (Exception ex){
                q.add(e);
                err++;
                if(err == q.size()){
                    throw ex;
                }
            }
        }
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
        return allParams;
    }

    public byte[] renderRendererJpeg(String identifier, HashMap<String, Object> params) throws Exception {
        var renderer = rendererService.findById(identifier).orElseThrow();
        return renderRendererJpeg(renderer, params);
    }

    private byte[] renderRendererJpeg(Renderer renderer, HashMap<String, Object> params) throws Exception {
        return renderTemplateJpeg(renderer.getTemplate(), generateParams(renderer, params));
    }

    public void printRenderer(String identifier, HashMap<String, Object> parameters, String clientIdentifier, String printService, Integer copies) throws Exception {
        clientService.printSvg(clientIdentifier, printService, renderRendererSvg(identifier, parameters), copies);
    }

    public void printTemplate(String identifier, HashMap<String, Object> parameters, String clientIdentifier, String printService, Integer copies) throws IOException, InterruptedException {
        clientService.printSvg(clientIdentifier, printService, renderTemplateSvg(identifier, parameters), copies);
    }
}
