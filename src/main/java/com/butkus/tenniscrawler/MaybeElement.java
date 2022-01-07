package com.butkus.tenniscrawler;

import lombok.Getter;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.NoSuchElementException;

public class MaybeElement {

    private final ChromeDriver driver;
    private final WebDriverWait wait;
    private final By by;

    @Getter
    private boolean found;
    private WebElement element;
    private File tempScreenshot;
    private String screenshotFileName;

    public MaybeElement(ChromeDriver driver, WebDriverWait wait, By by) {
        this.driver = driver;
        this.wait = wait;
        this.by = by;

        init();
    }

    private void init() {
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(by)
            ));
            element = driver.findElement(by);
            found = true;
        } catch (Exception e) {
            element = null;
            found = false;
            takeScreenshot();
            screenshotFileName = "Screenshot_" + Instant.now().toString().replace(":", "-") +
                    "  ---  " + by.toString();      // todo nicer name, dates
        }
    }

    public void refresh() {
        driver.navigate().refresh();
        init();
    }

    public void takeScreenshot(){
        tempScreenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
    }

    public void saveScreenshot(String additionalMsg) {
        if (found) throw new RuntimeException("Cannot create save screenshot because it was not taken.");

        // todo path from params
        String destPath = "C:\\Users\\vytas\\Downloads\\chrome screenshots\\" + screenshotFileName + " == " + additionalMsg + " === " + ".png";
        try {
            File destination = new File(destPath);
            org.apache.commons.io.FileUtils.copyFile(tempScreenshot, destination);
            System.out.println("Screenshot saved: " + destPath);
        } catch (IOException e) {
            System.out.println("FAILED TO SAVE screenshot: " + destPath);
            e.printStackTrace();
        }
    }

    public void click() {
        validateElementFound();
        element.click();
    }

    public void sendKeys(String string) {
        validateElementFound();
        element.sendKeys(string);
    }

    private void validateElementFound() {
        if (!found) throw new NoSuchElementException();
    }
}
