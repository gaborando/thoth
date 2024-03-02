package org.thoth.common;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.ScreenshotType;


public class Svg2Jpeg {

    public static byte[] convert(String svg, long delay) {
        for(int i = 0; i<3; i++) {
            try {
                try (Playwright playwright = Playwright.create()) {
                    try (Browser browser = playwright.chromium().launch()) {
                        try (var page = browser.newPage()) {
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
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
                System.out.println("Document Generation Failed. Attempt " + (i+1));
            }
        }
        throw new RuntimeException("Document Generation Failed after 3 attempts");
    }
}
