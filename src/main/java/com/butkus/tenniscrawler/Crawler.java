package com.butkus.tenniscrawler;

import org.javatuples.Pair;
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

@Component
public class Crawler {

    public static final int HARD = 2;
    public static final int CARPET = 8;

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

        List<Pair<LocalDate, Integer>> inputs = makeInputs();
        boolean foundAny = false;
        for (Pair<LocalDate, Integer> pair : inputs) {
            LocalDate date = pair.getValue0();
            String dateString = date.toString();    // todo date and dateString --> leave one
            Integer courtId = pair.getValue1();
            page.get(String.format("https://savitarna.tenisopasaulis.lt/rezervavimas/rezervavimas?sDate=%s&iPlaceId=%s", dateString, courtId));

            List<WebElement> slots = getAllTimeSlotsSeb(page);
            TimeTable timeTable = new TimeTable(slots, dateString, courtId);     // fixme: this step takes too long

            if (page.loggedInAsRegisteredUser()) {
                cache.addIfCacheable(date, courtId, timeTable.getAggregatedCourts());
            } else {
                timeTable.updateFromCache(cache);
            }

            boolean foundInCurrent = false;
            if (timeTable.isOfferFound()) {
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

    private static List<Pair<LocalDate, Integer>> makeInputs() {
        List<LocalDate> excludedDays = new ArrayList<>();
        excludedDays.addAll(getHolidays());
        excludedDays.addAll(getUnwantedDays());

        LocalDate date = LocalDate.now();
        List<Pair<LocalDate, Integer>> listHard = new ArrayList<>();
        List<Pair<LocalDate, Integer>> listCarpet = new ArrayList<>();
        for (int i=0; i<25; i++) {
            LocalDate currentDate = date.plusDays(i);
            if (excludedDays.contains(currentDate)) continue;
            Pair<LocalDate, Integer> pairHard = Pair.with(currentDate, HARD);
            Pair<LocalDate, Integer> pairCarpet = Pair.with(currentDate, CARPET);
            listHard.add(pairHard);
            listCarpet.add(pairCarpet);
        }

//        listCarpet.add(Pair.with(LocalDate.parse("2022-01-09"), CARPET));
        List<Pair<LocalDate, Integer>> list = new ArrayList<>();
        list.addAll(listHard);
        list.addAll(listCarpet);
        return list;
    }

    // todo make year-independent
    private static List<LocalDate> getHolidays() {
        List<LocalDate> result = new ArrayList<>();
        result.add(LocalDate.parse("2021-12-24"));
        result.add(LocalDate.parse("2021-12-25"));
        result.add(LocalDate.parse("2021-12-26"));

        result.add(LocalDate.parse("2022-01-01"));
        result.add(LocalDate.parse("2022-02-16"));
        result.add(LocalDate.parse("2022-03-11"));
        result.add(LocalDate.parse("2022-05-01"));
        result.add(LocalDate.parse("2022-06-24"));
        result.add(LocalDate.parse("2022-07-06"));
        result.add(LocalDate.parse("2022-08-15"));
        result.add(LocalDate.parse("2022-11-01"));
        result.add(LocalDate.parse("2022-11-02"));
        result.add(LocalDate.parse("2022-11-02"));
        result.add(LocalDate.parse("2022-12-24"));
        result.add(LocalDate.parse("2022-12-25"));
        result.add(LocalDate.parse("2022-12-26"));

        return result;
    }

    private static List<LocalDate> getUnwantedDays() {
        List<LocalDate> result = new ArrayList<>();
        result.add(LocalDate.parse("2022-01-05"));
        result.add(LocalDate.parse("2022-01-06"));      // THURSDAY -- > AREADY HAVE WEDNESDAY BOOKED
        result.add(LocalDate.parse("2022-01-09"));      // traded for saturday

        result.add(LocalDate.parse("2022-01-11"));      // 1900 secured

        result.add(LocalDate.parse("2022-01-07"));      // fridays
        result.add(LocalDate.parse("2022-01-14"));      // we generally
        result.add(LocalDate.parse("2022-01-21"));      // avoid fridays


        result.add(LocalDate.parse("2022-01-08"));      // booked already
        result.add(LocalDate.parse("2022-01-15"));
        result.add(LocalDate.parse("2022-01-22"));
        result.add(LocalDate.parse("2022-01-28"));
        result.add(LocalDate.parse("2022-01-29"));


        return result;
    }

    private static List<WebElement> getAllTimeSlotsSeb(Page page) {
        return page.findElements(By.id("jqReservationLink"));
    }

}
