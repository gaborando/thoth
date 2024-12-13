package com.thoth.common;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.ScreenshotType;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.microsoft.playwright.options.WaitUntilState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

public class MxGraph2Any {


    public static final Logger logger = LogManager.getLogger(MxGraph2Any.class);

    private final static String[] MINILA_ARGS = new String[]{
            "--disable-gpu",
            "--autoplay-policy=user-gesture-required",
            "--disable-background-networking",
            "--disable-background-timer-throttling",
            "--disable-backgrounding-occluded-windows",
            "--disable-breakpad",
            "--disable-client-side-phishing-detection",
            "--disable-component-update",
            "--disable-default-apps",
            "--disable-dev-shm-usage",
            "--disable-domain-reliability",
            "--disable-extensions",
            "--disable-features=AudioServiceOutOfProcess",
            "--disable-hang-monitor",
            "--disable-ipc-flooding-protection",
            "--disable-notifications",
            "--disable-offer-store-unmasked-wallet-cards",
            "--disable-popup-blocking",
            "--disable-print-preview",
            "--disable-prompt-on-repost",
            "--disable-renderer-backgrounding",
            "--disable-setuid-sandbox",
            "--disable-speech-api",
            "--disable-sync",
            "--hide-scrollbars",
            "--ignore-gpu-blacklist",
            "--metrics-recording-only",
            "--mute-audio",
            "--no-default-browser-check",
            "--no-first-run",
            "--no-pings",
            "--no-sandbox",
            "--no-zygote",
            "--password-store=basic",
            "--use-gl=swiftshader",
            "--use-mock-keychain",
    };


    public static byte[] convertDrawIoToPdf(String xml) throws IOException {
        return convertDrawIoToPdf(xml, UUID.randomUUID().toString());
    }

    public static byte[] convertDrawIoToPdf(String xml, String requestId) throws IOException {
        for (int i = 0; i < 3; i++) {
            logger.debug("{} - Convert attempt ({})", requestId, i + 1);
            try {
                try (Playwright playwright = Playwright.create()) {
                    logger.debug("{} - Playwright created {}", requestId, playwright.toString());
                    BrowserType.LaunchOptions options = new BrowserType.LaunchOptions()
                            .setHeadless(true) // Playwright's headless mode is boolean.
                            .setArgs(Arrays.stream(MINILA_ARGS).toList());
                    try (Browser browser = playwright.chromium().launch(options)) {
                        logger.debug("{} - Playwright {} - browser launched {}", requestId, playwright.toString(), browser);
                        try (var page = browser.newPage()) {
                            logger.debug("{} - Playwright {} - browser {} - page created {}", requestId, playwright.toString(), browser, page);
                            page.navigate("https://viewer.diagrams.net/export3.html", new Page.NavigateOptions().setWaitUntil(WaitUntilState.NETWORKIDLE));
                            logger.debug("{} - Playwright {} - browser {} - page  {} - Content set!", requestId, playwright.toString(), browser, page);
                            page.evaluate("""
                                            (arg) => {return render(arg);}
                                            """,
                                    Map.of("xml", xml,
                                            "format", "pdf",
                                            "fit", true,
                                            "crop", true,
                                            "pageMargin", 0));
                            logger.debug("{} - Playwright {} - browser {} - page  {} - Before rendering script Executed!", requestId, playwright.toString(), browser, page);
                            // Wait for the DOMContentLoaded event
                            page.waitForSelector("#LoadingComplete", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.ATTACHED));
                            logger.debug("{} - Playwright {} - browser {} - page  {} - DOM content is loaded.", requestId, playwright.toString(), browser, page);

                            // Optionally, wait for the network to be idle (all network requests are finished)
                            page.waitForLoadState(LoadState.NETWORKIDLE);
                            logger.debug("{} - Playwright {} - browser {} - page  {} - Network is idle.", requestId, playwright.toString(), browser, page);

                            var bounds  =(Map<String, Number>) page.mainFrame().evalOnSelector("#LoadingComplete", "div => JSON.parse(div.getAttribute('bounds'))");
                            var w = (int) Math.ceil(bounds.get("width").doubleValue() + bounds.get("x").doubleValue());
                            var h = (int) Math.ceil(bounds.get("height").doubleValue() + bounds.get("y").doubleValue());
                            page.setViewportSize(w, h);
                            logger.debug("{} - Playwright {} - browser {} - page  {} - Rendering Done!", requestId, playwright.toString(), browser, page);
                            var resp = page.pdf(
                                    new Page.PdfOptions().setPreferCSSPageSize(true).setPrintBackground(true)
                            );
                            logger.debug("{} - Playwright {} - browser {} - page  {} - Pdf Generated Taken!", requestId, playwright.toString(), browser, page);
                            return resp;
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("Document Generation Failed. Attempt " + (i + 1), e);
            }
        }
        throw new RuntimeException("Document Generation Failed after 3 attempts");
    }


    public static byte[] convertDrawIoToJpeg(String xml) throws IOException {
        return convertDrawIoToJpeg(xml, UUID.randomUUID().toString());
    }

    public static byte[] convertDrawIoToJpeg(String xml, String requestId) throws IOException {
        for(int i = 0; i<3; i++) {
            logger.debug("{} - Convert attempt ({})", requestId, i+1);
            try {
                try (Playwright playwright = Playwright.create()) {
                    logger.debug("{} - Playwright created {}", requestId, playwright.toString());
                    BrowserType.LaunchOptions options = new BrowserType.LaunchOptions()
                            .setHeadless(true) // Playwright's headless mode is boolean.
                            .setArgs(Arrays.stream(MINILA_ARGS).toList());
                    try (Browser browser = playwright.chromium().launch(options)) {
                        logger.debug("{} - Playwright {} - browser launched {}", requestId, playwright.toString(), browser);
                        try (var page = browser.newPage()) {
                            logger.debug("{} - Playwright {} - browser {} - page created {}", requestId, playwright.toString(), browser, page);
                            page.navigate("https://viewer.diagrams.net/export3.html", new Page.NavigateOptions().setWaitUntil(WaitUntilState.NETWORKIDLE));
                            logger.debug("{} - Playwright {} - browser {} - page  {} - Content set!", requestId, playwright.toString(), browser, page);
                            page.evaluate("""
                                            (arg) => {return render(arg);}
                                            """,
                                    Map.of("xml", xml,
                                            "format", "jpeg",
                                            "fit", true,
                                            "crop", true,
                                            "pageMargin", 0));
                            logger.debug("{} - Playwright {} - browser {} - page  {} - Before rendering script Executed!", requestId, playwright.toString(), browser, page);
                            // Wait for the DOMContentLoaded event
                            page.waitForSelector("#LoadingComplete", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.ATTACHED));
                            logger.debug("{} - Playwright {} - browser {} - page  {} - DOM content is loaded.", requestId, playwright.toString(), browser, page);

                            // Optionally, wait for the network to be idle (all network requests are finished)
                            page.waitForLoadState(LoadState.NETWORKIDLE);
                            logger.debug("{} - Playwright {} - browser {} - page  {} - Network is idle.", requestId, playwright.toString(), browser, page);


                            var bounds  =(Map<String, Number>) page.mainFrame().evalOnSelector("#LoadingComplete", "div => JSON.parse(div.getAttribute('bounds'))");
                            var w = (int) Math.ceil(bounds.get("width").doubleValue() + bounds.get("x").doubleValue());
                            var h = (int) Math.ceil(bounds.get("height").doubleValue() + bounds.get("y").doubleValue());
                            page.setViewportSize(w, h);
                            logger.debug("{} - Playwright {} - browser {} - page  {} - Rendering Done!", requestId, playwright.toString(), browser, page);
                            var resp = page.screenshot(
                                    new Page.ScreenshotOptions().setType(ScreenshotType.JPEG)
                                            .setQuality(100)
                                            .setFullPage(false)
                            );
                            logger.debug("{} - Playwright {} - browser {} - page  {} - Screenshot Taken!", requestId, playwright.toString(), browser, page);
                            return resp;
                        }
                    }
                }
            }
            catch (Exception e){
                logger.error("Document Generation Failed. Attempt " + (i+1), e);
            }
        }
        throw new RuntimeException("Document Generation Failed after 3 attempts");
    }
}
