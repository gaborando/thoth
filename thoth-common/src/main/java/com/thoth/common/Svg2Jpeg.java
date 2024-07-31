package com.thoth.common;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.ScreenshotType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Svg2Jpeg {

    public static final Logger logger = LogManager.getLogger(Svg2Jpeg.class);


    public static void warmup(){
        logger.info("Warming up Playwright");
        try(var p = Playwright.create()){
            logger.info("Playwright Warm-up - Playwright created");
            try (Browser browser = p.chromium().launch()) {
                logger.info("Playwright Warm-up - Browser created");
                try (var page = browser.newPage()){
                    logger.info("Playwright Warm-up - Page created {}", page);
                }
            }
        }
    }

    public static byte[] convert(String svg, String requestId) {
        for(int i = 0; i<3; i++) {
            logger.debug("{} - Convert attempt ({})", requestId, i+1);
            try {
                try (Playwright playwright = Playwright.create()) {
                    logger.debug("{} - Playwright created {}", requestId, playwright.toString());
                    try (Browser browser = playwright.chromium().launch()) {
                        logger.debug("{} - Playwright {} - browser launched {}", requestId, playwright.toString(), browser);
                        try (var page = browser.newPage()) {
                            logger.debug("{} - Playwright {} - browser {} - page created {}", requestId, playwright.toString(), browser, page);
                            page.setContent(svg);
                            logger.debug("{} - Playwright {} - browser {} - page  {} - Content set!", requestId, playwright.toString(), browser, page);
                            page.evaluate("""
                                    () => {
                                        var scale = 2;
                                        var svg = document.getElementsByTagName('svg')[0];
                                        var iWidth = svg.width.baseVal.value;
                                        var iHeight = svg.height.baseVal.value;
                                        svg.setAttribute('preserveAspectRatio', 'xMidYMid meet');
                                        svg.removeAttribute('width');
                                        svg.removeAttribute('height');
                                        svg.style.setProperty('margin', 0, 'important');
                                        svg.style.setProperty('border', 0, 'important');
                                        svg.style.setProperty('padding', 0, 'important');
                                        svg.style.setProperty('position', 'fixed', 'important');
                                        svg.style.setProperty('left', 0, 'important');
                                        svg.style.setProperty('top', 0, 'important');
                                        svg.style.setProperty('width', (iWidth * scale) + 'px', 'important');
                                        svg.style.setProperty('height', (iHeight * scale) + 'px',  'important');
                                    }
                                    """);
                            logger.debug("{} - Playwright {} - browser {} - page  {} - Before rendering script Executed!", requestId, playwright.toString(), browser, page);
                            // Wait for the DOMContentLoaded event
                            page.waitForLoadState(LoadState.DOMCONTENTLOADED);
                            logger.debug("{} - Playwright {} - browser {} - page  {} - DOM content is loaded.", requestId, playwright.toString(), browser, page);

                            // Optionally, wait for the network to be idle (all network requests are finished)
                            page.waitForLoadState(LoadState.NETWORKIDLE);
                            logger.debug("{} - Playwright {} - browser {} - page  {} - Network is idle.", requestId, playwright.toString(), browser, page);

                            // Wait for all images to be completely loaded
                            page.waitForFunction("() => Array.from(document.images).every(img => img.complete && img.naturalHeight !== 0)");
                            logger.debug("{} - Playwright {} - browser {} - page  {} - All Images Loaded.", requestId, playwright.toString(), browser, page);

                            logger.debug("{} - Playwright {} - browser {} - page  {} - Rendering Done!", requestId, playwright.toString(), browser, page);
                            var resp =  page.locator("svg")
                                    .screenshot(new Locator.ScreenshotOptions()
                                    .setType(ScreenshotType.JPEG)
                                    .setQuality(100));
                            logger.debug("{} - Playwright {} - browser {} - page  {} - Screenshot Taken!", requestId, playwright.toString(), browser, page);
                            return resp;
                        }
                    }
                }
            }catch (Exception e){
                logger.error("Document Generation Failed. Attempt " + (i+1), e);
            }
        }
        throw new RuntimeException("Document Generation Failed after 3 attempts");
    }
}
