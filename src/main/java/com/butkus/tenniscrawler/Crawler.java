package com.butkus.tenniscrawler;

import org.javatuples.Triplet;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.butkus.tenniscrawler.Court.CARPET;
import static com.butkus.tenniscrawler.Court.HARD;
import static com.butkus.tenniscrawler.ExtensionInterest.*;

@Component
public class Crawler {

    private final AudioPlayer audioPlayer;
    private final Page page;
    private final Cache cache;
    private final Random random;
    private final Duration sleepUpTo;

    @Autowired
    public Crawler(AudioPlayer audioPlayer,
                   Page page,
                   Cache cache,
                   @Value("${app.sleep-up-to}") Duration sleepUpTo) throws NoSuchAlgorithmException {
        this.audioPlayer = audioPlayer;
        this.page = page;
        this.cache = cache;
        this.sleepUpTo = sleepUpTo;
        this.random = SecureRandom.getInstanceStrong();
    }

    @Scheduled(cron = "${app.cron}")
    public void run() throws InterruptedException {
        try {
            crawlEverythingOnce(page);
        } finally {
            page.close();
        }
    }

    private void crawlEverythingOnce(Page page) throws InterruptedException {
        int sleepSeconds = random.nextInt((int) sleepUpTo.toSeconds());
        TimeUnit.SECONDS.sleep(sleepSeconds);
        Instant start;

        boolean cacheStale = cache.isStale();
        if (cacheStale) {
            cache.clearCache();
            page.login(Page.UserType.REGISTERED_USER);
            start = Instant.now();
            System.out.printf("Logged in as <<<REGISTERED USER>>> slept for %s seconds, crawl started at %s%n",
                    sleepSeconds, getTimeString(start));
        } else {
            page.login(Page.UserType.ANONYMOUS_USER);
            start = Instant.now();
            System.out.printf("Logged in as <<<ANONYMOUS USER>>> slept for %s seconds, crawl started at %s. Cache expires in %s minutes%n",
                    sleepSeconds, getTimeString(start), cache.durationToLive().toMinutes());
        }

        boolean foundAny = false;
        for (Triplet<LocalDate, Integer, ExtensionInterest> dayAtCourt : makeInputs()) {
            page.loadDayAtCourt(dayAtCourt);
            List<WebElement> slots = getAllTimeSlotsSeb(page);
            TimeTable timeTable = new TimeTable(slots, dayAtCourt);     // fixme: this step takes too long

            if (page.loggedInAsRegisteredUser()) {
                cache.addIfCacheable(dayAtCourt, timeTable.getAggregatedCourts());
            } else {
                timeTable.updateFromCache(cache);
            }

            boolean foundInCurrent = false;
            if (timeTable.isOfferFound(cache)) {        // todo isOfferFound() to return enum(X for free new time, LATER for later found, EARIEL for earlier found, etc... (so that to later log out if specific improvement found or not)
                foundAny = true;
                foundInCurrent = true;
            }
            String foundNotFoundMark = foundInCurrent ? "‹✔›" : " \uD83D\uDFA8 ";   // IntelliJ UTF-8 console output issue: https://stackoverflow.com/a/56430344
            System.out.printf("%-54s %s%n", timeTable.getReadableAggregatedCourt(), foundNotFoundMark);     // fixme replace arbitrary 54
        }

        if (cacheStale) {      // todo rework to be less dependent on order. now before-if, withing-if, and after-if operations are interrwined to work correctly. Would be nice to have cache work independently
            cache.setUpdated();
        }

        if (foundAny) audioPlayer.playSound();

        printCrawlEndTime(start);
    }

    private void printCrawlEndTime(Instant start) {
        Instant end = Instant.now();
        long jobLengthInSeconds = start.until(end, ChronoUnit.SECONDS);
        System.out.printf("Crawl took %s seconds and finished at %s%n", jobLengthInSeconds, getTimeString(end));
    }

    private String getTimeString(Instant start) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(" HH:mm:ss");
        LocalDateTime localDateTime = start.atZone(ZoneId.of("Europe/Vilnius")).toLocalDateTime();
        return localDateTime.format(formatter);
    }

    private static List<Triplet<LocalDate, Integer, ExtensionInterest>> makeInputs() {        // todo: input: same as now + ExtensionInterest(EarlierThanBooked, LaterThanBooked, Both)
        List<Triplet<LocalDate, Integer, ExtensionInterest>> specialDays = new ArrayList<>();
        specialDays.addAll(getHolidays());
        specialDays.addAll(getExceptionDays());

        LocalDate date = LocalDate.now();
        List<Triplet<LocalDate, Integer, ExtensionInterest>> listHard = new ArrayList<>();
        List<Triplet<LocalDate, Integer, ExtensionInterest>> listCarpet = new ArrayList<>();
        for (int i=0; i<25; i++) {
            LocalDate currentDate = date.plusDays(i);
            boolean currentDateInSpecialDays = specialDays.stream().anyMatch(e -> e.getValue0().equals(currentDate));
            if (currentDateInSpecialDays) {
                List<Triplet<LocalDate, Integer, ExtensionInterest>> hard = specialDays.stream().filter(e -> e.getValue0().equals(currentDate)).filter(e -> e.getValue1().equals(HARD) && e.getValue2() != NONE).collect(Collectors.toList());
                List<Triplet<LocalDate, Integer, ExtensionInterest>> carpet = specialDays.stream().filter(e -> e.getValue0().equals(currentDate)).filter(e -> e.getValue1().equals(CARPET) && e.getValue2() != NONE).collect(Collectors.toList());
                listHard.addAll(hard);
                listCarpet.addAll(carpet);
            } else {
                Triplet<LocalDate, Integer, ExtensionInterest> hard = Triplet.with(currentDate, HARD, ANY);
                Triplet<LocalDate, Integer, ExtensionInterest> carpet = Triplet.with(currentDate, CARPET, ANY);
                listHard.add(hard);
                listCarpet.add(carpet);
            }
        }

//        listCarpet.add(Triplet.with(LocalDate.parse("2022-01-30"), CARPET, BOTH));
        List<Triplet<LocalDate, Integer, ExtensionInterest>> list = new ArrayList<>();
        list.addAll(listHard);
        list.addAll(listCarpet);
        return list;
    }

    // todo make year-independent
    private static List<Triplet<LocalDate, Integer, ExtensionInterest>> getHolidays() {
        List<Triplet<LocalDate, Integer, ExtensionInterest>> holidays = new ArrayList<>();

        addExclusions(holidays, "2021-12-24");
        addExclusions(holidays, "2021-12-25");
        addExclusions(holidays, "2021-12-26");

        addExclusions(holidays, "2022-01-01");
        addExclusions(holidays, "2022-02-16");
        addExclusions(holidays, "2022-03-11");
        addExclusions(holidays, "2022-05-01");
        addExclusions(holidays, "2022-06-24");
        addExclusions(holidays, "2022-07-06");
        addExclusions(holidays, "2022-08-15");
        addExclusions(holidays, "2022-11-01");
        addExclusions(holidays, "2022-11-02");
        addExclusions(holidays, "2022-11-02");
        addExclusions(holidays, "2022-12-24");
        addExclusions(holidays, "2022-12-25");
        addExclusions(holidays, "2022-12-26");

        return holidays;
    }

    private static List<Triplet<LocalDate, Integer, ExtensionInterest>> getExceptionDays() {
        List<Triplet<LocalDate, Integer, ExtensionInterest>> exceptionDays = new ArrayList<>();

        addExclusions(exceptionDays, "2022-01-14");        // friday

        addExclusions(exceptionDays, "2022-01-15");        // saturday, secured, not interedted in updating
//        exceptionDays.add(Triplet.with(LocalDate.parse("2022-01-15"), HARD, EARLIER));      // 19:30 secured
//        exceptionDays.add(Triplet.with(LocalDate.parse("2022-01-15"), CARPET, EARLIER));      // 19:30 secured

        addExclusions(exceptionDays, "2022-01-16");        // have day before already
        exceptionDays.add(Triplet.with(LocalDate.parse("2022-01-17"), HARD, LATER));        // fixme: If I don't add this, 2022-01-17 HARD won't be cached (will be skipped)
        exceptionDays.add(Triplet.with(LocalDate.parse("2022-01-17"), CARPET, LATER));      // fixme: but add this, and 2022-01-17 CARPET will say:  Requested LATER for date=2022-01-17 and court=Kilimas (courtId=8) but no existing booking

        addExclusions(exceptionDays, "2022-01-21");        // friday

        addExclusions(exceptionDays, "2022-01-22");        // saturday
        addExclusions(exceptionDays, "2022-01-23");        // sunday, booked and happy with
//        exceptionDays.add(Triplet.with(LocalDate.parse("2022-01-23"), HARD, BOTH));      // 19:30 secured
//        exceptionDays.add(Triplet.with(LocalDate.parse("2022-01-23"), CARPET, BOTH));      // 19:30 secured

        addExclusions(exceptionDays, "2022-01-28");     // dovile stand-by, already booked with Delfi
        addExclusions(exceptionDays, "2022-01-29");        // saturday

        addExclusions(exceptionDays, "2022-02-04");        // friday, booked on delfi
        addExclusions(exceptionDays, "2022-02-05");        // saturday, not interested

        return exceptionDays;
    }

    private static void addExclusions(List<Triplet<LocalDate, Integer, ExtensionInterest>> result, String date) {
        result.add(Triplet.with(LocalDate.parse(date), HARD, NONE));
        result.add(Triplet.with(LocalDate.parse(date), CARPET, NONE));
    }

    private static List<WebElement> getAllTimeSlotsSeb(Page page) {
        return page.findElements(By.id("jqReservationLink"));       // fixme: `by` is seb-specific, it should be in `page`
    }

}
