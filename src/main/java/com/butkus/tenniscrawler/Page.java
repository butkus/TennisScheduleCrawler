package com.butkus.tenniscrawler;

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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

@Component
public class Page {

    public static final String YRA_LAISVO = "yraLaisvo";
    public static final String YRA_PARDUODAMO = "yraParduodamo";
    public static final String YRA_NUPIRKTO = "yraNupirkto";

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

    public void loadMainBookingPage() {
        if (loggedInAs == UserType.LOGGED_OUT) {
            login(UserType.ANONYMOUS_USER);
        }

        driver.get("https://book.sebarena.lt/#/rezervuoti/tenisas");

        MaybeWebElement popupCloseButton = findElement(By.className("closeBtn"));
        if (popupCloseButton.isFound()) popupCloseButton.click();

        MaybeWebElement chooseFilterElement = findElement(
                By.xpath("//*[text()='Pasirinkite filtrus, kurie visada bus saugomi Jūsų vartotojo aplinkoje.']//../..//*[@class='btn blue min']"));
        chooseFilterElement.click();

        MaybeWebElement filter60mins = findElement(By.xpath("//*[text()='60 min']/../.."));
        filter60mins.click();

        MaybeWebElement filterEveningOnly = findElement(By.xpath("//*[text()='Vakaras 15:00 - 23:00']/../.."));
        filterEveningOnly.click();

        MaybeWebElement filterCloseButton = findElement(By.className("closeBtn"));
        if (filterCloseButton.isFound()) filterCloseButton.click();
    }

    public void login(UserType userType) {
        init();
        driver.get("https://book.sebarena.lt");
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
        MaybeWebElement main_page_content = findElement(By.className("main_page_content"));
        WebElement table = main_page_content.getAMaybe().findElement(By.className("table"));
        WebElement header = table.findElement(By.className("header"));
        List<WebElement> headerDivsWithDates = header.findElements(By.xpath("./child::*"));
        List<LocalDate> dates = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        int todaysMonth = now.getMonth().getValue();
        int todaysDayOfMonth = now.getDayOfMonth();
        for (int i = 1; i < headerDivsWithDates.size(); i++) {
            String dateString = headerDivsWithDates.get(i).getText();   // e.g. 10/08
            String[] split = dateString.split("/");
            if (split.length < 2) break;

            int currentMonth = Integer.parseInt(split[0]);
            int currentDayOfMonth = Integer.parseInt(split[1]);
            boolean isToday = currentMonth == todaysMonth && currentDayOfMonth == todaysDayOfMonth;

            boolean monthInFuture = currentMonth > todaysMonth;
            boolean sameMonthButDayInFuture = currentMonth == todaysMonth && currentDayOfMonth > todaysDayOfMonth;
            boolean isFuture = monthInFuture || sameMonthButDayInFuture;

            if (isToday || isFuture) {
                dates.add(LocalDate.of(now.getYear(), currentMonth, currentDayOfMonth));
            } else {
                dates.add(LocalDate.of(now.getYear() + 1, currentMonth, currentDayOfMonth));
            }
        }

        List<WebElement> timeColumn = table.findElements(By.xpath("div[contains(@class, 'left')]/div[1]/child::*"));
        boolean soundPlayed = false;
        int slotCountInDay = table.findElements(By.xpath(String.format("//div[contains(@class, 'laikai')]/div[1]/div[1]/child::*"))).size();
        for (int dateIteration = 0; dateIteration < dates.size(); dateIteration++) {
            System.out.printf("%n%n--- TIMES FOR " + dates.get(dateIteration) + " ---%n");

            for (int timeIteration = 0; timeIteration < slotCountInDay; timeIteration++) {
                String time = timeColumn.get(timeIteration).getText();

                Optional<WebElement> slotOpt = getSlot(table, dateIteration, timeIteration);
                if (slotOpt.isEmpty()) {
                    System.out.println(time + "  nėra laisvų");
                } else {
                    WebElement slot = slotOpt.get();
                    String className = slot.getAttribute("class");
                    boolean hasFreeCourts = className.contains(YRA_LAISVO);
                    boolean paidByMe = className.contains(YRA_NUPIRKTO);
                    String noOfFreeCourts = slot.findElement(By.xpath("span[1]")).getText();
                    if (paidByMe && hasFreeCourts) {
                        System.out.printf("%s  MANO nupirktas BEI yra laisvų: %s %n", time, noOfFreeCourts);
                    } else if (paidByMe) {
                        System.out.printf("%s  MANO nupirktas laikas %n", time);
                    } else if (hasFreeCourts) {
                        System.out.printf("%s  laisvų: %s %n", time, noOfFreeCourts);
                        
                        LocalDate nextWednesday = LocalDate.of(2022, 10, 19);
                        LocalDate nextNextWednesday = LocalDate.of(2022, 10, 26);
                        LocalDate currDate = dates.get(dateIteration);
                        boolean next2Wednesdays = currDate.equals(nextWednesday) || currDate.equals(nextNextWednesday);
                        boolean between6And8pm = List.of(6, 7, 8, 9, 10).contains(timeIteration);
                        if (next2Wednesdays && between6And8pm) {
                            if (!soundPlayed) {
                                soundPlayed = true;
                                audioPlayer.playSound();
                            }
                        }

                    } else {
                        System.out.println(" === NEI LAISVAS, NEI TURI LAISVŲ KORTŲ, NEI MANO NUPIRKTAS ===");
                    }

                }
            }
        }

//



//        MaybeWebElements maybeWebElements = new MaybeWebElements(driver, wait, By.id("jqReservationLink"));
//        while(!maybeWebElements.isFound()) {
//            maybeWebElements.saveScreenshot("getting time slots");
//            maybeWebElements.refresh();
//        }
//        return maybeWebElements.get();
                return null;
    }

    private Optional<WebElement> getSlot(WebElement table, int dateIteration, int i) {
        try {
            String slotXpath = String.format("//div[contains(@class, 'laikai')]/div[%s]/div[1]/div[%s]/div[1]/div[1]", dateIteration + 1, i + 1);   // fixme: getClass() dependent on outside iterator
            WebElement slot = table.findElement(By.xpath(slotXpath));
            return Optional.of(slot);
        } catch (org.openqa.selenium.NoSuchElementException noSuchElementException) {
            return Optional.empty();
        }
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
        MaybeWebElement usernameTextField = findElement(By.xpath("//*[text()='Prisijungimo vardas']/../..//input"));
        MaybeWebElement passwordTextField = findElement(By.xpath("//*[text()='Slaptažodis']/../..//input"));
        usernameTextField.sendKeys(sebUsername);
        passwordTextField.sendKeys(sebPassword);

        MaybeWebElement loginButton = findElement(By.xpath("//*[text()='Prisijungti']/../.."));
        loginButton.click();

        MaybeWebElement popupCloseButton = findElement(By.className("closeBtn"));
        if (popupCloseButton.isFound()) popupCloseButton.click();
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
