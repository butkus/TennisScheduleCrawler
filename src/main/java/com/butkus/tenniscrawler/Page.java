package com.butkus.tenniscrawler;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Component
public class Page {

    private final String chromeDriverPath;
    private final String sebUsername;
    private final String sebPassword;

    private ChromeDriver driver;
    private WebDriverWait wait;

    private boolean loggedIn;       // todo remove
    private UserType loggedInAs;

    @Autowired
    public Page(@Value("${app.chrome-driver-path}") File chromeDriverPath,
                @Value("${app.seb-username}") String sebUsername,
                @Value("${app.seb-password}") String sebPassword) {
        this.chromeDriverPath = chromeDriverPath.getPath();
        this.sebUsername = sebUsername;
        this.sebPassword = sebPassword;
        loggedIn = false;
        loggedInAs = UserType.LOGGED_OUT;
    }

    public void get(String url) {
        if (!loggedIn) {
            login(UserType.ANONYMOUS_USER);
        }
        driver.get(url);
    }

    public void login(UserType userType) {
        init();
        driver.get("https://savitarna.tenisopasaulis.lt/vartotojas/prisijungimas#anonymus-login");
        loginAsUser(userType);
        loggedIn = true;
    }

    private void init() {
        this.driver = createWebDriver();
        this.wait = createDriverWait(driver);
    }

    private void loginAsUser(UserType userType) {       // todo unify terms
        if (userType == UserType.REGISTERED_USER) {
            authorizedLogin();
            loggedInAs = UserType.REGISTERED_USER;
        } else if (userType == UserType.ANONYMOUS_USER) {
            anonymousLogin();
            loggedInAs = UserType.ANONYMOUS_USER;
        }
    }

    public Optional<WebElement> findElement(By findBy) {
        Optional<WebElement> result;
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(findBy)
            ));
            result = Optional.of(driver.findElement(findBy));
        } catch (Exception e) {
            result = Optional.empty();
            String screenshotFileName = "Screenshot_" + Instant.now().toString().replace(":", "-");
            String screenshotPathOrError = captureScreenshot(driver, screenshotFileName);
            System.out.println("screenshotPathOrError = " + screenshotPathOrError);
        }
        return result;
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
        WebElement anonymousLoginIcon = findElement(By.xpath("//a[contains(@href, '#anonymus-login')]")).get(); // fixme can be no element
        anonymousLoginIcon.click();
        Optional<WebElement> boxPopclose = findElement(By.id("boxPopclose"));
        boxPopclose.ifPresent(WebElement::click);
    }

    private void authorizedLogin() {
        WebElement loginIcon = findElement(By.xpath("//a[contains(@href, '#normal-login')]")).get();
        loginIcon.click();

        WebElement usernameTextField = findElement(By.id("LoginForm_var_login")).get();
        WebElement passwordTextField = findElement(By.id("LoginForm_var_password")).get();
        usernameTextField.sendKeys(sebUsername);
        passwordTextField.sendKeys(sebPassword);

        WebElement loginButton = findElement(By.xpath("//form[@id='login_form']/div[4]/input")).get();
        loginButton.click();
    }

    public void close() {
//        driver.close();
        driver.quit();      // todo quit driver, just close page (need to close chromedriver on app shutdown)
        loggedIn = false;
        loggedInAs = UserType.LOGGED_OUT;
    }

    public boolean loggedInAsRegisteredUser() {
        return loggedInAs == UserType.REGISTERED_USER;
    }

    public static String captureScreenshot (WebDriver driver, String screenshotName){

        try {
            TakesScreenshot ts = (TakesScreenshot)driver;
            File source = ts.getScreenshotAs(OutputType.FILE);
            String dest = "C:\\Users\\vytas\\Downloads\\chrome screenshots\\" + screenshotName + ".png";
            File destination = new File(dest);
            org.apache.commons.io.FileUtils.copyFile(source, destination);
            return dest;
        }

        catch (IOException e) {
            return e.getMessage();
        }
    }

    public enum UserType {
        REGISTERED_USER, ANONYMOUS_USER, LOGGED_OUT
    }
}
