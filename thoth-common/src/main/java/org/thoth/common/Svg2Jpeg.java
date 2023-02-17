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

public class Svg2Jpeg {

    private final Page page;

    public Svg2Jpeg() {
        Playwright playwright = Playwright.create();
        Browser browser = playwright.chromium().launch();
        page = browser.newPage();
    }

    public synchronized byte[] convert(String svg) {
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
        return page.locator("svg").screenshot(new Locator.ScreenshotOptions()
                .setType(ScreenshotType.JPEG)
                .setQuality(100));
    }
}
