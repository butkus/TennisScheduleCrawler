package com.butkus.tenniscrawler;

import org.javatuples.Triplet;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;

@Component
public class Page {

    private final String chromeDriverPath;
    private final String sebUsername;
    private final String sebPassword;

    private ChromeDriver driver;
    private WebDriverWait wait;

    private UserType loggedInAs;

    @Autowired
    public Page(@Value("${app.chrome-driver-path}") File chromeDriverPath,
                @Value("${app.seb-username}") String sebUsername,
                @Value("${app.seb-password}") String sebPassword) {
        this.chromeDriverPath = chromeDriverPath.getPath();
        this.sebUsername = sebUsername;
        this.sebPassword = sebPassword;
        loggedInAs = UserType.LOGGED_OUT;
    }

    public void loadDayAtCourt(Triplet<LocalDate, Integer, ExtensionInterest> dayAtCourt) {
        if (loggedInAs == UserType.LOGGED_OUT) {
            login(UserType.ANONYMOUS_USER);
        }
        LocalDate date = dayAtCourt.getValue0();
        Integer courtId = dayAtCourt.getValue1();
        String url = String.format("https://savitarna.tenisopasaulis.lt/rezervavimas/rezervavimas?sDate=%s&iPlaceId=%s", date.toString(), courtId);
        driver.get(url);
    }

    public void login(UserType userType) {
        init();
        driver.get("https://savitarna.tenisopasaulis.lt");
        loginAsUser(userType);
    }

    private void init() {
        this.driver = createWebDriver();
        this.wait = createDriverWait(driver);
    }

    private void loginAsUser(UserType userType) {
        if (userType == UserType.REGISTERED_USER) {
            authorizedLogin();
            loggedInAs = UserType.REGISTERED_USER;
        } else if (userType == UserType.ANONYMOUS_USER) {
            anonymousLogin();
            loggedInAs = UserType.ANONYMOUS_USER;
        }
    }

    public MaybeWebElement findElement(By findBy) {
        return new MaybeWebElement(driver, wait, findBy);
    }

    public List<WebElement> getAllTimeSlots() {
        MaybeWebElements maybeWebElements = new MaybeWebElements(driver, wait, By.id("jqReservationLink"));
        while(!maybeWebElements.isFound()) {
            maybeWebElements.saveScreenshot("getting time slots");
            maybeWebElements.refresh();
        }
        return maybeWebElements.get();
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
        return tryCreateChromeDriver(options);
    }

    private ChromeDriver tryCreateChromeDriver(ChromeOptions options) {
        try {
            return new ChromeDriver(options);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void anonymousLogin() {
        MaybeWebElement anonymousLoginIcon = findElement(By.xpath("//a[contains(@href, '#anonymus-login')]"));
        while (!anonymousLoginIcon.isFound()) {
            anonymousLoginIcon.saveScreenshot("anonymous-login");
            anonymousLoginIcon.refresh();
        }
        anonymousLoginIcon.click();

        MaybeWebElement boxPopclose = findElement(By.id("boxPopclose"));
        if (boxPopclose.isFound()) boxPopclose.click();
    }

    private void authorizedLogin() {
        MaybeWebElement usernameTextField = findElement(By.id("LoginForm_var_login"));
        MaybeWebElement passwordTextField = findElement(By.id("LoginForm_var_password"));
        usernameTextField.sendKeys(sebUsername);
        passwordTextField.sendKeys(sebPassword);

        MaybeWebElement loginButton = findElement(By.xpath("//form[@id='login_form']/div[4]/input"));
        loginButton.click();
    }

    public void close() {
//        driver.close();
        driver.quit();      // todo quit driver, just close page (need to close chromedriver on app shutdown)
        loggedInAs = UserType.LOGGED_OUT;
    }

    public boolean loggedInAsRegisteredUser() {
        return loggedInAs == UserType.REGISTERED_USER;
    }

    public enum UserType {
        REGISTERED_USER, ANONYMOUS_USER, LOGGED_OUT
    }
}
