package org.thoth.common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

public class Svg2Jpeg {

    public final String exePath;

    public final String outPath;

    public Svg2Jpeg(String exePath, String outPath) {
        this.exePath = exePath;
        this.outPath = outPath;
    }

    public byte[] convert(String svg) throws IOException, InterruptedException {
        var fName = UUID.randomUUID().toString();
        var svgPath = Path.of(outPath, fName + ".svg");
        var jpegPath = Path.of(outPath, fName + ".jpeg");
        Files.write(svgPath, svg.getBytes(), StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW);
        var pb = new ProcessBuilder();
        pb.command(exePath, svgPath.toAbsolutePath().toString(), jpegPath.toAbsolutePath().toString(), "2x");
        var p = pb.start();
        int rc = p.waitFor();
        if (rc == 0) {
            var resp = new String(p.getInputStream().readAllBytes());
            System.out.println(resp);
        } else {
            var error = new String(p.getErrorStream().readAllBytes());
            System.err.println(error);
        }
        Files.delete(svgPath);
        var bytes = Files.readAllBytes(jpegPath);
        Files.delete(jpegPath);
        return bytes;
    }
}
