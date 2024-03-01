package org.thoth.common;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.ScreenshotType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Svg2Jpeg {

    private static Playwright playwright = Playwright.create();
    private static Browser browser = playwright.chromium().launch();

    private static synchronized Page getPage() {
        try {
            return browser.newPage();
        }catch (Exception e){
            try {
                browser.close();
            }catch (Exception ignored){}
            try {
                playwright.close();
            } catch (Exception ignored) {
            }
            playwright = Playwright.create();
            browser = playwright.chromium().launch();
            System.gc();
            return browser.newPage();
        }
    }


    public static byte[] convert(String svg, long delay) {
        for (int i = 0; i < 3; i++) {
            try {
                try (var page = getPage()) {
                    page.setContent(svg);
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
                    Thread.sleep(delay);
                    return page.locator("svg").screenshot(new Locator.ScreenshotOptions()
                            .setType(ScreenshotType.JPEG)
                            .setQuality(100));
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Document Generation Failed. Attempt " + (i + 1));
            }
        }
        throw new RuntimeException("Document Generation Failed after 3 attempts");
    }

}
