package com.butkus.tenniscrawler;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class MaybeWebElement extends Maybe<WebElement> {

    public MaybeWebElement(ChromeDriver driver, WebDriverWait wait, By by) {
        super(driver, wait, by);
    }

    @Override
    protected void loadAMaybe() {
        aMaybe = driver.findElement(by);
    }

    public void click() {
        validateElementFound();
        aMaybe.click();
    }

    public void sendKeys(String string) {
        validateElementFound();
        aMaybe.sendKeys(string);
    }

    // todo delete
    public String getUnderpinnings() {
        return aMaybe.getText();
    }

    // todo delete
    public WebElement getAMaybe() {
        return aMaybe;
    }

}
