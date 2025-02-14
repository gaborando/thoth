package com.thoth.client;

import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPrintable;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.thoth.common.Jpeg2Pdf;
import com.thoth.common.Svg2Jpeg;
import com.thoth.common.dto.PrintRequest;
import com.thoth.common.dto.RegisterClientRequest;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.standard.MediaPrintableArea;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class ClientService {
    private static final Logger logger = LogManager.getLogger(ClientService.class);


    private final String clientName;
    @Getter
    private final String clientIdentifier;
    private final String ownerSID;

    private final Counter printCount = new Counter();

    private final Set<String> printQueue = new ConcurrentSkipListSet<>();

    private final String[] exclude;
    private final CountDownLatch clientRegisteredLatch = new CountDownLatch(1);

    public ClientService(RabbitTemplate rabbitTemplate,
                         @Value("${thoth.client.name}") String clientName,
                         @Value("${thoth.client.identifier}") String clientIdentifier,
                         @Value("${thoth.client.owner.sid}") String ownerSID,
                         @Value("${thoth.client.exclude:;}") String exclude) {
        this.clientName = clientName;
        this.clientIdentifier = clientIdentifier;
        this.ownerSID = ownerSID;
        this.exclude = exclude.split(";");

    }


    public Exception printSvg(PrintRequest printRequest) {
        clientRegisteredLatch.countDown();
        var printCount = this.printCount.incrementAndGet();
        var requestId = printRequest.getPrintService() + '-' + ZonedDateTime.now() + '-' + printCount;
        logger.debug("{} - Print Request Received ({})", requestId, printCount);
        logger.debug("{} - Requested Printer: {}", requestId, printRequest.getPrintService());
        if ("NONE".equalsIgnoreCase(printRequest.getPrintService())) {
            logger.debug("{} - Document Skipped", requestId);
            return null;
        }
        var jobReference = new AtomicReference<PrinterJob>();
        try {
            PrintService printService = Arrays.stream(PrintServiceLookup.lookupPrintServices(null, null))
                    .filter(s -> s.getName().equals(printRequest.getPrintService()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Print service not found: " + printRequest.getPrintService()));
            logger.debug("{} - Starting PDF Generation", requestId);
            var img = Svg2Jpeg.convert(printRequest.getSvg(), requestId);
            var pdf = Jpeg2Pdf.convert(img);
            logger.debug("{} - PDF Generated", requestId);
            PDDocument document = Loader.loadPDF(pdf);
            logger.debug("{} - PDF Document loaded", requestId);
            printQueue.add(requestId);
            logger.debug("{} - Printing Request Added to Queue (Pending: {})", requestId, printQueue.size());
            var done = new CompletableFuture<Exception>();
            var task = new Thread(() -> {
                try {
                    logger.debug("{} - Obtaining printer Job", requestId);
                    var start = System.currentTimeMillis();
                    PrinterJob job = PrinterJob.getPrinterJob();
                    jobReference.set(job);
                    logger.debug("{} - Printer Job Obtained ({}ms)", requestId, System.currentTimeMillis() - start);
                    job.setPrintService(printService);
                    logger.debug("{} - Print Service Set", requestId);
                    PageFormat pf = job.defaultPage();
                    logger.debug("{} - Default Page Obtained", requestId);
                    HashPrintRequestAttributeSet attr = new HashPrintRequestAttributeSet();
                    if (document.getPage(0).getBBox().getWidth() <= document.getPage(0).getBBox().getHeight()) {
                        pf.setOrientation(PageFormat.PORTRAIT);
                        attr.add(new MediaPrintableArea(0f, 0f, 210f, 297f, MediaPrintableArea.MM));
                    } else {
                        pf.setOrientation(PageFormat.LANDSCAPE);
                        attr.add(new MediaPrintableArea(0f, 0f, 210f, 297f, MediaPrintableArea.MM));
                    }
                    var printable = new PDFPrintable(document);
                    logger.debug("{} - Printable document generated", requestId);
                        job.setPrintable(printable, pf);
                    logger.debug("{} - Printable document set to Job Request", requestId);
                    job.setCopies(printRequest.getCopies());
                    logger.debug("QUEUES: {} - job.print called (Pending: {})", requestId, printQueue.size());
                    start = System.currentTimeMillis();
                    job.print(attr);
                    logger.debug("{} - Printing Request Sent  (Pending: {}) ({}ms)", requestId, printQueue.size(), System.currentTimeMillis() - start);
                    done.complete(null);
                } catch (Exception e) {
                    logger.error(requestId + " - Printing Request Failed", e);
                    done.completeExceptionally(e);
                }
            });
            task.start();
            Exception result = null;
            try {
                result = done.get(30, TimeUnit.SECONDS);
            }catch (TimeoutException e){
                result = e;
                var job = jobReference.get();
                if(job != null){
                    try {
                        logger.debug("{} - Cancelling job", requestId);
                        job.cancel();
                    }catch (Exception e1){
                        logger.error(requestId + " - Error on job cancelling", e);
                    }
                }
                try {
                    logger.debug("{} - Interrupting task", requestId);
                    task.interrupt();
                }catch (Exception e1){
                    logger.error(requestId + " - Error on interrupting task", e);
                }
            }catch (Exception e){
                logger.error(requestId + " - Error on document printing", e);
                result = e;
            }
            printQueue.remove(requestId);
            return result;
        } catch (Exception e) {
            printQueue.remove(requestId);
            logger.error(requestId + " - Error on document printing", e);
            return e;
        }

    }

    public void register(Consumer<RegisterClientRequest> registerClientRequestConsumer) {
        var request = new RegisterClientRequest();
        request.setIdentifier(clientIdentifier);
        request.setName(clientName);
        request.setOwnerSID(ownerSID);
        request.setPrintServices(Arrays.stream(PrintServiceLookup.lookupPrintServices(null, null))
                .map(PrintService::getName)
                .filter(n -> Arrays.stream(exclude).noneMatch(e -> e.equals(n)))
                .collect(Collectors.toList()));
        request.getPrintServices().add("NONE");
        logger.info("Registering client {} [{}]", request.getName(), request.getIdentifier());
        logger.info("Detected PrintServices:");
        for (String printService : request.getPrintServices()) {
            logger.info(" -   {}", printService);
        }
        registerClientRequestConsumer.accept(request);
        clientRegisteredLatch.countDown();

    }
}
