package com.thot.client;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.printing.Orientation;
import org.apache.pdfbox.printing.PDFPrintable;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaSize;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Paper;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileInputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;

@Component
public class Runner implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService printService : printServices) {
            System.err.println(printService);
            if(printService.getName().equals("Microsoft Print to PDF")){
                PDDocument document = Loader.loadPDF(new File("C:\\Users\\ggalazzo\\Downloads\\pdf.pdf"));
                PrinterJob job = PrinterJob.getPrinterJob();
                job.setPrintService(printService);
                PageFormat pf = job.defaultPage();
                Paper paper = new Paper();
                double margin = 18; // 1 inch margin
                paper.setImageableArea(margin, margin, paper.getWidth() - margin * 2, paper.getHeight() - margin * 2);
                if(document.getPage(0).getBBox().getWidth() >= document.getPage(0).getBBox().getHeight()){
                    pf.setOrientation(0);
                }else{
                    pf.setOrientation(1);
                }
                pf.setPaper(paper);
                job.setPrintable(new PDFPrintable(document), pf);
                job.print();
            }
        }
    }
}
