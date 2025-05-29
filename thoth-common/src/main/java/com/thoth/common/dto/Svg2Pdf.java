package com.thoth.common.dto;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;


import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

public class Svg2Pdf {

    public static final Logger logger = LogManager.getLogger(Svg2Pdf.class);

    public static byte[] convert(String svg, String requestId) throws IOException {


        /*
        // Convert SVG to PDF using Apache Batik
        ByteArrayInputStream svgStream = new ByteArrayInputStream(svg.getBytes());
        TranscoderInput input = new TranscoderInput(svgStream);
        TranscoderOutput output = new TranscoderOutput(pdfOut);

        PDFTranscoder transcoder = new CustomPDFTranscoder();

        // Convert and save to PDF
        transcoder.transcode(input, output);

        pdfOut.flush();
        pdfOut.close();

        System.out.println("PDF with SVG and images created successfully!");*/

        /*

        FileOutputStream pdfOut = new FileOutputStream("graph_with_image.pdf");

        HtmlConverter.convertToPdf(svg,pdfOut);

        pdfOut.flush();
        pdfOut.close();*/

        String htmlFile = "example.html";
        String pdfFile = "output.pdf";

        try (FileOutputStream outputStream = new FileOutputStream(new File(pdfFile))) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(svg, "");
            builder.toStream(outputStream);
            builder.run();
            System.out.println("PDF created: " + pdfFile);
        } catch (Exception e) {
            e.printStackTrace();
        }




        return null;
    }

    public static void main(String[] args) throws IOException {

        Svg2Pdf.convert(
                Files.readString(Path.of("C:\\Users\\ggalazzo\\Downloads\\svg.svg")),
                ""
        );
    }
}
