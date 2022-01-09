package com.butkus.tenniscrawler;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.logging.Level;

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
        driver.get("https://savitarna.tenisopasaulis.lt");
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

    public MaybeElement findElement(By findBy) {
        return new MaybeElement(driver, wait, findBy);
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

        System.setProperty("webdriver.chrome.args", "--disable-logging");
        System.setProperty(ChromeDriverService.CHROME_DRIVER_SILENT_OUTPUT_PROPERTY, "true");
        java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--window-size=1920,1200");
        return new ChromeDriver(options);
    }

    private void anonymousLogin() {
        MaybeElement anonymousLoginIcon = findElement(By.xpath("//a[contains(@href, '#anonymus-login')]"));
        while (!anonymousLoginIcon.isFound()) {
            anonymousLoginIcon.saveScreenshot("anonymous-login");
            anonymousLoginIcon.refresh();
        }
        anonymousLoginIcon.click();

        MaybeElement boxPopclose = findElement(By.id("boxPopclose"));
        if (boxPopclose.isFound()) boxPopclose.click();
    }

    private void authorizedLogin() {
        MaybeElement usernameTextField = findElement(By.id("LoginForm_var_login"));
        MaybeElement passwordTextField = findElement(By.id("LoginForm_var_password"));
        usernameTextField.sendKeys(sebUsername);
        passwordTextField.sendKeys(sebPassword);

        MaybeElement loginButton = findElement(By.xpath("//form[@id='login_form']/div[4]/input"));
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

    public enum UserType {
        REGISTERED_USER, ANONYMOUS_USER, LOGGED_OUT
    }
}
