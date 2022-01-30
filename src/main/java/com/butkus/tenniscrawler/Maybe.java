package com.butkus.tenniscrawler;

import lombok.Getter;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.NoSuchElementException;

public abstract class Maybe<T> {

    protected final ChromeDriver driver;
    protected final WebDriverWait wait;

    protected final By by;
    protected T aMaybe;
    @Getter private boolean found;

    private File tempScreenshot;
    private String screenshotFileName;

    Maybe(ChromeDriver driver, WebDriverWait wait, By by) {
        this.driver = driver;
        this.wait = wait;
        this.by = by;
        init();
    }

    protected void init() {
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(by)
            ));
            loadAMaybe();
            found = true;
        } catch (Exception e) {
            System.out.println("Failed to load element " + by.toString());
            aMaybe = null;
            found = false;
            takeScreenshot();
            screenshotFileName = "Screenshot_" + Instant.now().toString().replace(":", "-") +
                    "  ---  " + by.toString().replace(":", "-");      // todo nicer name, dates
        }
    }

    protected abstract void loadAMaybe();

    public void refresh() {
        driver.navigate().refresh();
        init();
    }

    private void takeScreenshot(){
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

    protected void validateElementFound() {
        if (!found) throw new NoSuchElementException("Element not found: " + by.toString());
    }

}
