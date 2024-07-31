package com.thoth.common;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static java.lang.System.in;

public class Jpeg2Pdf {
    public static byte[] convert(byte[] img) throws IOException {
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
}
