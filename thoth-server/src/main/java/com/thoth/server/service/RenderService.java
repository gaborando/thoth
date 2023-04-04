package com.thoth.server.service;

import com.thoth.server.configuration.security.SecuredTimestampService;
import com.thoth.server.controller.dto.renderer.Association;
import com.thoth.server.model.domain.Renderer;
import com.thoth.server.model.domain.Template;
import org.springframework.stereotype.Service;
import org.thoth.common.Jpeg2Pdf;
import org.thoth.common.Svg2Jpeg;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RenderService {

    private final TemplateService templateService;

    private final RendererService rendererService;

    private final DataSourceService dataSourceService;
    private final Svg2Jpeg svg2Jpeg;

    private final ClientService clientService;

    private final SecuredTimestampService securedTimestampService;

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
        svg2Jpeg = new Svg2Jpeg();
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

        final String regex = "\\{\\{([a-zA-Z0-9\\._]+)(\\|[a-zA-Z0-9]+(:[a-zA-Z0-9_'-\\.]+)*)*\\}\\}";

        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(svg);

        var markers = new HashSet<Marker>();

        while (matcher.find()) {

            var m = new Marker();
            m.definition = matcher.group(0);
            m.parameter = matcher.group(1);
            markers.add(m);
        }
        for (Marker marker : markers) {
            svg = svg.replace(marker.definition, allParams.get(marker.parameter) == null ? "" :  marker.compute(allParams.get(marker.parameter) .toString()));
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

    public void printRenderer(String identifier, HashMap<String, Object> parameters, String clientIdentifier, String printService, Integer copies) throws Exception {
        clientService.printSvg(clientIdentifier, printService, renderRendererSvg(identifier, parameters), copies);
    }

    public void printTemplate(String identifier, HashMap<String, Object> parameters, String clientIdentifier, String printService, Integer copies) throws IOException, InterruptedException {
        clientService.printSvg(clientIdentifier, printService, renderTemplateSvg(identifier, parameters), copies);
    }

    private class Marker {
        String definition;
        String parameter;

        String compute(String value){
            var parts = definition.replace("{{","").replace("}}","").split("\\|");
            if(parts.length == 1){
                return value;
            }
            var pipe = parts[1].split(":");
            return switch (pipe[0]){
                case "padding" -> paddingPipe(value, Integer.parseInt(pipe[1]), pipe[2].charAt(0));
                case "date" -> datePipe(value, pipe[1]);
                case "trim" -> trimPipe(value);
                case "number" -> numberPipe(value, pipe[1]);
                default -> value;
            };
        }

        String paddingPipe(String value, int size, char character){
            return String.format("%" + size + "s", value).replace(' ', character);
        }

        String datePipe(String value, String pattern){
            return ZonedDateTime.parse(value).format(DateTimeFormatter.ofPattern(pattern));
        }

        String trimPipe(String value){
            return value.trim();
        }

        String numberPipe(String value, String format){
            return String.format("%" + format + "f", Double.parseDouble(value));
        }
        @Override
        public int hashCode() {
            return definition.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Marker marker = (Marker) o;
            return Objects.equals(definition, marker.definition);
        }

        @Override
        public String toString() {
            return definition+" - "+parameter;
        }
    }
}
