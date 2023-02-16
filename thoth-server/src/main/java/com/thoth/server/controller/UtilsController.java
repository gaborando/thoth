package com.thoth.server.controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.oned.EAN13Writer;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
@RequestMapping("/utils")
public class UtilsController {

    @GetMapping(value = "/barcode", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> renderBarcode(
            @RequestParam String code,
            @RequestParam(defaultValue = "300") int width,
            @RequestParam(defaultValue = "150") int height) throws IOException {

        Code128Writer barcodeWriter = new Code128Writer();
        BitMatrix bitMatrix = barcodeWriter.encode(code.replace("{","").replace("}",""),
                BarcodeFormat.CODE_128, width, height);

        var img =  MatrixToImageWriter.toBufferedImage(bitMatrix);
        var baos = new ByteArrayOutputStream();
        ImageIO.write(img, "jpeg", baos);
        return ResponseEntity.ok(baos.toByteArray());
    }

    @GetMapping(value = "/qrcode", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> renderQrcode(@RequestParam String code,
            @RequestParam(defaultValue = "300") int size) throws WriterException, IOException {

        QRCodeWriter  barcodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = barcodeWriter.encode(code, BarcodeFormat.QR_CODE, size, size);

        var img =  MatrixToImageWriter.toBufferedImage(bitMatrix);
        var baos = new ByteArrayOutputStream();
        ImageIO.write(img, "jpeg", baos);
        return ResponseEntity.ok(baos.toByteArray());
    }
}
