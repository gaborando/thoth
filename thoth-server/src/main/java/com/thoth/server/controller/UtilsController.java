package com.thoth.server.controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.nimbusds.jose.shaded.gson.Gson;
import com.thoth.server.configuration.security.SecuredTimestampService;
import com.thoth.server.service.SidService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.GlyphVector;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.AttributedString;
import java.util.HashMap;
import java.util.List;

import static com.google.zxing.EncodeHintType.GS1_FORMAT;
import static com.google.zxing.EncodeHintType.MARGIN;

@RestController
@RequestMapping("/utils")
public class UtilsController {

    private final SidService sidService;

    private final SecuredTimestampService securedTimestampService;

    public UtilsController(SidService sidService, SecuredTimestampService securedTimestampService) {
        this.sidService = sidService;
        this.securedTimestampService = securedTimestampService;
    }


    @SuppressWarnings("unchecked")
    @GetMapping(value = "/barcode", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> renderBarcode(
            @RequestParam String code,
            @RequestParam(name = "TMP_KEY", defaultValue = "") String key,
            @RequestParam(defaultValue = "true") boolean gs1,
            @RequestParam(defaultValue = "{}") String hints,
            @RequestParam(defaultValue = "CODE_128") BarcodeFormat format,
            @RequestParam(defaultValue = "300") int width,
            @RequestParam(defaultValue = "150") int height) throws IOException, WriterException {

        var safe = true;
        try {
            securedTimestampService.parse(key);
        } catch (Exception e) {
            safe = false;
            code = "(01)01234567890128(10)000000AAA8";
        }

        code = code.replace("{", "").replace("}", "");

        if(code.isEmpty()){
            BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
            var baos = new ByteArrayOutputStream();
            ImageIO.write(image, "jpeg", baos);
            return ResponseEntity.ok(baos.toByteArray());
        }

        var jHints = new Gson().fromJson(hints, HashMap.class);
        var hi = new HashMap<EncodeHintType, Object>();
        jHints.keySet().forEach(k -> {
            hi.put(EncodeHintType.valueOf(k.toString()), jHints.get(k.toString()));
        });
        if (!hi.containsKey(MARGIN)) {
            hi.put(MARGIN, 0);
        }
        if (gs1) {
            if ((format == BarcodeFormat.QR_CODE || format == BarcodeFormat.DATA_MATRIX) && code.startsWith("(")) {
                code = code.substring(1);
            }
            code = code.replace('(', (format == BarcodeFormat.QR_CODE || format == BarcodeFormat.DATA_MATRIX)
                    ? (char)29 : '\u00F1').replace(")", "");
            hi.put(GS1_FORMAT, true);
        }

        var matrix = new MultiFormatWriter().encode(code, format, width, height, hi);
        var img = MatrixToImageWriter.toBufferedImage(matrix);
        if (!safe) {
            var w = (int) (img.getWidth() * 0.6);
            var h = (int) (img.getHeight() * 0.6);
            var g = img.getGraphics();
            g.fillRect((img.getWidth() - w) / 2, (img.getHeight() - h) / 2, w, h);
            Font font = new Font("Arial", Font.BOLD, 48);

            var text = format.toString();
            if (gs1) {
                text += " - GS1";
            }
            AttributedString attributedText = new AttributedString(text);
            attributedText.addAttribute(TextAttribute.FOREGROUND, Color.BLACK);
            FontMetrics metrics = g.getFontMetrics(font);

            GlyphVector vector = font.createGlyphVector(metrics.getFontRenderContext(), text);
            Shape outline = vector.getOutline(0, 0);
            double expectedWidth = outline.getBounds().getWidth();
            double expectedHeight = outline.getBounds().getHeight();


            w = (int) (img.getWidth() * 0.5);
            h = (int) (img.getHeight() * 0.5);
            boolean textFits = w >= expectedWidth && h >= expectedHeight;


            if (!textFits) {
                double widthBasedFontSize = (font.getSize2D() * w) / expectedWidth;
                double heightBasedFontSize = (font.getSize2D() * h) / expectedHeight;

                double newFontSize = Math.min(widthBasedFontSize, heightBasedFontSize);
                font = font.deriveFont(font.getStyle(), (float) newFontSize);
            }
            attributedText.addAttribute(TextAttribute.FONT, font);
            metrics = g.getFontMetrics(font);
            int positionX = (img.getWidth() - metrics.stringWidth(text)) / 2;
            int positionY = (img.getHeight() - metrics.getHeight()) / 2 + metrics.getAscent();
            g.drawString(attributedText.getIterator(), positionX, positionY);
        }
        var baos = new ByteArrayOutputStream();
        ImageIO.write(img, "jpeg", baos);
        return ResponseEntity.ok(baos.toByteArray());
    }

    @Secured({"ROLE_USER", "ROLE_API"})
    @GetMapping(value = "/tmp-key", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> generateTempKey(@RequestParam(defaultValue = "60") int duration) throws WriterException, IOException {
        return ResponseEntity.ok(securedTimestampService.generate(duration));
    }

    @Secured("ROLE_USER")
    @GetMapping(value = "/user-autocomplete", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> userAutocomplete(@RequestParam String value) {
        return ResponseEntity.ok(sidService.findAllUsers(value, PageRequest.of(0, 5)));
    }

    @Secured("ROLE_USER")
    @GetMapping(value = "/organization-autocomplete", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> organizationAutocomplete(@RequestParam String value) {
        return ResponseEntity.ok(sidService.findAllOrganizations(value, PageRequest.of(0, 5)));
    }
}
