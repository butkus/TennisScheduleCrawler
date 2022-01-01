package com.butkus.tenniscrawler;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@Component
public class Page {

    private final String chromeDriverPath;
    private final String sebUsername;
    private final String sebPassword;

    private ChromeDriver driver;
    private WebDriverWait wait;

    private boolean loggedIn;

    @Autowired
    public Page(@Value("${app.chrome-driver-path}") File chromeDriverPath,
                @Value("${app.seb-username}") String sebUsername,
                @Value("${app.seb-password}") String sebPassword) {
        this.chromeDriverPath = chromeDriverPath.getPath();
        this.sebUsername = sebUsername;
        this.sebPassword = sebPassword;
        loggedIn = false;
    }

    public void get(String url) {
        if (!loggedIn) {
            init();
            driver.get("https://savitarna.tenisopasaulis.lt/vartotojas/prisijungimas#anonymus-login");
            login();
            loggedIn = true;
        }
        driver.get(url);
    }

    private void init() {
        this.driver = createWebDriver();
        this.wait = createDriverWait(driver);
    }

    private void login() {
//        authorizedLogin();      //  todo: crawl as much as possible via anonymous login;
        anonymousLogin();
    }

    public WebElement findElement(By findBy) {
        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(findBy)
        ));
        return driver.findElement(findBy);
    }

    public List<WebElement> findElements(By findBy) {
        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(findBy)
        ));
        return driver.findElements(findBy);
    }

    private static WebDriverWait createDriverWait(WebDriver driver) {
        return new WebDriverWait(driver, 15L);
    }

    private ChromeDriver createWebDriver() {
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        System.setProperty("webdriver.chrome.whitelistedIps", "");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--window-size=1920,1200");
        return new ChromeDriver(options);
    }

    private void anonymousLogin() {
        WebElement anonymousLoginIcon = findElement(By.xpath("//a[contains(@href, '#anonymus-login')]"));
        anonymousLoginIcon.click();
        WebElement boxPopclose = findElement(By.id("boxPopclose"));
        boxPopclose.click();
    }

    private void authorizedLogin() {
        WebElement loginIcon = findElement(By.xpath("//a[contains(@href, '#normal-login')]"));
        loginIcon.click();

        WebElement usernameTextField = findElement(By.id("LoginForm_var_login"));
        WebElement passwordTextField = findElement(By.id("LoginForm_var_password"));
        usernameTextField.sendKeys(sebUsername);
        passwordTextField.sendKeys(sebPassword);

        WebElement loginButton = findElement(By.xpath("//form[@id='login_form']/div[4]/input"));
        loginButton.click();
    }

    public void close() {
//        driver.close();
        driver.quit();      // todo quit driver, just close page (need to close chromedriver on app shutdown)
        loggedIn = false;
    }

}
