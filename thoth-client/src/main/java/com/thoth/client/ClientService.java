package com.thoth.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPrintable;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thoth.common.Jpeg2Pdf;
import org.thoth.common.Svg2Jpeg;
import org.thoth.common.dto.PrintRequest;
import org.thoth.common.dto.RegisterClientRequest;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.standard.MediaPrintableArea;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

@Service
public class ClientService {
    private static final Logger logger = LogManager.getLogger(ClientService.class);
    private final Svg2Jpeg svg2Jpeg;

    private final RabbitTemplate rabbitTemplate;

    private final String clientName;
    private final String clientIdentifier;
    private final DirectExchange serverExchange;
    private final String ownerSID;

    public ClientService(RabbitTemplate rabbitTemplate,
                         @Value("${thoth.client.name}") String clientName,
                         @Value("${thoth.client.identifier}") String clientIdentifier,
                         @Value("${thoth.client.owner.sid}") String ownerSID,
                         @Value("${thoth.server.exchange}") String serverExchange) {
        this.rabbitTemplate = rabbitTemplate;
        this.clientName = clientName;
        this.clientIdentifier = clientIdentifier;
        this.svg2Jpeg = new Svg2Jpeg();
        this.ownerSID = ownerSID;
        this.serverExchange = new DirectExchange(serverExchange);

    }

    @RabbitListener(queues = "thoth.${thoth.client.identifier}.rpc.requests")
    public boolean printSvg(PrintRequest printRequest) throws IOException, InterruptedException, PrinterException {
        logger.info("Print Request Received");
        logger.info("Requested Printer: " + printRequest.getPrintService());
        if("NONE".equalsIgnoreCase(printRequest.getPrintService())){
            logger.info("Document Skipped");
            return true;
        }
        try {
            PrintService printService = Arrays.stream(PrintServiceLookup.lookupPrintServices(null, null))
                    .filter(s -> s.getName().equals(printRequest.getPrintService())).findFirst().orElseThrow();

            logger.info("Starting PDF Generation");
            var img = svg2Jpeg.convert(printRequest.getSvg());
            var pdf = Jpeg2Pdf.convert(img);
            logger.info("PDF Generated");
            PDDocument document = Loader.loadPDF(pdf);
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintService(printService);
            PageFormat pf = job.defaultPage();
            HashPrintRequestAttributeSet attr = new HashPrintRequestAttributeSet();
            if (document.getPage(0).getBBox().getWidth() <= document.getPage(0).getBBox().getHeight()) {
                pf.setOrientation(PageFormat.PORTRAIT);
                attr.add(new MediaPrintableArea(0f, 0f, 210f, 297f, MediaPrintableArea.MM));
            } else {
                pf.setOrientation(PageFormat.LANDSCAPE);
                attr.add(new MediaPrintableArea(0f, 0f, 210f, 297f, MediaPrintableArea.MM));
            }
            job.setPrintable(new PDFPrintable(document), pf);
            job.setCopies(printRequest.getCopies());
            logger.info("Sending Printing Request...");
            job.print(attr);
            logger.info("Document Printed");
            return true;
        }catch (Exception e){
            logger.error(e);
            return false;
        }
    }

    public void register() {
        var request = new RegisterClientRequest();
        request.setIdentifier(clientIdentifier);
        request.setName(clientName);
        request.setOwnerSID(ownerSID);
        request.setPrintServices(Arrays.stream(PrintServiceLookup.lookupPrintServices(null, null))
                .map(PrintService::getName).collect(Collectors.toList()));
        request.getPrintServices().add("NONE");
        logger.info("Registering client " + request.getName() + " ["+request.getIdentifier()+"]");
        logger.info("Detected PrintServices:");
        for (String printService : request.getPrintServices()) {
            logger.info(" -   " + printService);
        }
        rabbitTemplate.convertSendAndReceive(serverExchange.getName(), "rpc", request);
    }
}
