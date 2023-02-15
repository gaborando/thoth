package com.thot.server.service;

import com.thot.server.controller.dto.renderer.Association;
import com.thot.server.model.domain.Renderer;
import com.thot.server.model.domain.Template;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thot.common.Svg2Jpeg;
import org.thot.common.Svg2Pdf;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static java.lang.System.in;

@Service
public class RenderService {

    private final TemplateService templateService;

    private final RendererService rendererService;

    private final DataSourceService dataSourceService;
    private final Svg2Jpeg svg2Jpeg;

    public RenderService(
            TemplateService templateService,
            RendererService rendererService,
            DataSourceService dataSourceService,
            @Value("${thot.out.dir}") String outDir,
            @Value("${thot.svgexport.executable}") String executable

    ) {
        this.templateService = templateService;
        this.rendererService = rendererService;
        this.dataSourceService = dataSourceService;
        svg2Jpeg = new Svg2Jpeg(executable, outDir);
    }

    public String renderTemplateSvg(String identifier, HashMap<String, Object> params) throws IOException, InterruptedException {

        var template = templateService.getById(identifier).orElseThrow();
        return renderTemplateSvg(template, params);
    }

    public String renderTemplateSvg(Template template, HashMap<String, Object> params) throws IOException, InterruptedException {

        var svg = template.getSvg();
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
        PDDocument document = new PDDocument();
        BufferedImage bimg = ImageIO.read(new ByteArrayInputStream(img));
        float width = bimg.getWidth();
        float height = bimg.getHeight();
        PDPage page = new PDPage(new PDRectangle(width, height));
        document.addPage(page);
        var i = PDImageXObject.createFromByteArray(document, img, null);
        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        contentStream.drawImage(i, 0, 0);
        contentStream.close();
        in.close();

        var out = new ByteArrayOutputStream();
        document.save(out);
        document.close();
        return out.toByteArray();
    }

    public byte[] renderRendererPdf(String identifier, HashMap<String, Object> params) throws IOException, InterruptedException {
        var img = renderRendererJpeg(identifier, params);
        PDDocument document = new PDDocument();
        BufferedImage bimg = ImageIO.read(new ByteArrayInputStream(img));
        float width = bimg.getWidth();
        float height = bimg.getHeight();
        PDPage page = new PDPage(new PDRectangle(width, height));
        document.addPage(page);
        var i = PDImageXObject.createFromByteArray(document, img, null);
        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        contentStream.drawImage(i, 0, 0);
        contentStream.close();
        in.close();

        var out = new ByteArrayOutputStream();
        document.save(out);
        document.close();
        return out.toByteArray();
    }


    public String renderRendererSvg(String identifier, HashMap<String, Object> params) throws IOException, InterruptedException {
        var renderer = rendererService.findById(identifier).orElseThrow();
        return renderRendererSvg(renderer, params);
    }

    public String renderRendererSvg(Renderer renderer, HashMap<String, Object> params) throws IOException, InterruptedException {
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

    public byte[] renderRendererJpeg(String identifier, HashMap<String, Object> params) throws IOException, InterruptedException {
        var renderer = rendererService.findById(identifier).orElseThrow();
        return renderRendererJpeg(renderer, params);
    }

    private byte[] renderRendererJpeg(Renderer renderer, HashMap<String, Object> params) throws IOException, InterruptedException {


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
}
