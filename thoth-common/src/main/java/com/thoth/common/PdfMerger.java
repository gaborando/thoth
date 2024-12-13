package com.thoth.common;

import org.apache.pdfbox.multipdf.PDFMergerUtility;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.stream.Stream;

public class PdfMerger {

    public static byte[] merge(Stream<byte[]> pdfByteArrays) throws IOException {
        PDFMergerUtility pdfMerger = new PDFMergerUtility();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Set the output stream to store the merged PDF
        pdfMerger.setDestinationStream(outputStream);

        // Add each byte array as an InputStream source to the merger
        for (byte[] pdfBytes : pdfByteArrays.toList()) {
            pdfMerger.addSource(new ByteArrayInputStream(pdfBytes));
        }

        // Merge the PDFs
        pdfMerger.mergeDocuments(null);

        // Return the merged PDF as a byte array
        return outputStream.toByteArray();
    }
}
