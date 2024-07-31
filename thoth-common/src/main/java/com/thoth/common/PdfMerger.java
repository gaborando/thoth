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
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

import static java.lang.System.in;

public class PdfMerger {

    public static byte[] merge(Stream<byte[]> images) throws IOException {
        PDDocument document = new PDDocument();
        images.map(img -> {
            try {
                BufferedImage bimg = ImageIO.read(new ByteArrayInputStream(img));
                float width = bimg.getWidth();
                float height = bimg.getHeight();
                PDPage page = new PDPage(new PDRectangle(width, height));

                var i = PDImageXObject.createFromByteArray(document, img, null);
                PDPageContentStream contentStream = new PDPageContentStream(document, page);
                contentStream.drawImage(i, 0, 0);
                contentStream.close();
                return page;
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }).filter(Objects::nonNull).forEach( p -> {
            synchronized (document){
                document.addPage(p);
            }
        });
        in.close();
        var out = new ByteArrayOutputStream();
        document.save(out);
        document.close();
        return out.toByteArray();
    }
}
