package org.thot.common;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class Svg2Pdf {

    public final String binPath;

    public final String outPath;

    public Svg2Pdf(String binPath, String outPath) {
        this.binPath = binPath;
        this.outPath = outPath;
    }

    public byte[] convert(String svg) throws IOException, InterruptedException {
        var fName = UUID.randomUUID().toString();
        var svgPath = Path.of(outPath, fName + ".svg");
        var pdfPath = Path.of(outPath, fName + ".pdf");
        Files.write(svgPath, svg.getBytes());
        var pb = new ProcessBuilder();
        pb.command(binPath + "\\wkhtmltopdf.exe", svgPath.toAbsolutePath().toString(), pdfPath.toAbsolutePath().toString() );
        var p = pb.start();
        int rc = p.waitFor();
        if(rc == 0){
            var resp = new String(p.getInputStream().readAllBytes());
            System.out.println(resp);
        }else{
            var error = new String(p.getErrorStream().readAllBytes());
            System.err.println(error);
        }
        Files.delete(svgPath);
        var bytes =  Files.readAllBytes(pdfPath);
        Files.delete(pdfPath);
        return bytes;
    }
}
