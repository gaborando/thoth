package com.thoth.server.controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.thoth.server.configuration.security.SecuredTimestampService;
import com.thoth.server.service.SidService;
import org.springdoc.core.service.SecurityService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/utils")
public class UtilsController {

    private final SidService sidService;

    private final SecuredTimestampService securedTimestampService;

    public UtilsController(SidService sidService, SecuredTimestampService securedTimestampService) {
        this.sidService = sidService;
        this.securedTimestampService = securedTimestampService;
    }

    @Secured({"ROLE_USER", "ROLE_TMP"})
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

    @Secured({"ROLE_USER", "ROLE_TMP"})
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

    @Secured({"ROLE_USER", "ROLE_API"})
    @GetMapping(value = "/tmp-key", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> generateTempKey(@RequestParam(defaultValue = "60") int duration) throws WriterException, IOException {
        return ResponseEntity.ok(securedTimestampService.generate(duration));
    }

    @Secured("ROLE_USER")
    @GetMapping(value = "/user-autocomplete", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> userAutocomplete(@RequestParam String value)  {
        return ResponseEntity.ok(sidService.findAllUsers(value, PageRequest.of(0,5)));
    }

    @Secured("ROLE_USER")
    @GetMapping(value = "/organization-autocomplete", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> organizationAutocomplete(@RequestParam String value)  {
        return ResponseEntity.ok(sidService.findAllOrganizations(value, PageRequest.of(0,5)));
    }
}
