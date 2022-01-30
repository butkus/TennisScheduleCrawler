package com.butkus.tenniscrawler;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class MaybeWebElements extends Maybe<List<WebElement>> {

    public MaybeWebElements(ChromeDriver driver, WebDriverWait wait, By by) {
        super(driver, wait, by);
    }

    @Override
    protected void loadAMaybe() {
        aMaybe = driver.findElements(by);
    }

    public List<WebElement> get() {
        validateElementFound();
        return aMaybe;
    }
}
