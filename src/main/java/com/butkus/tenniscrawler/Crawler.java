package com.butkus.tenniscrawler;

import org.javatuples.Triplet;
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
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.butkus.tenniscrawler.ExtensionInterest.NONE;

@Component
public class Crawler {

    private final AudioPlayer audioPlayer;
    private final Page page;
    private final Cache cache;
    private final BackwardsCounter forcedRefresh;
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
        this.forcedRefresh = new BackwardsCounter();
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
        if (cacheStale || forcedRefresh.isOn()) {
            forcedRefresh.decrement();
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
        for (Triplet<LocalDate, Integer, ExtensionInterest> dayAtCourt : Input.makeInputs()) {
            if (dayAtCourt.getValue2() == NONE && !page.loggedInAsRegisteredUser()) continue;

            page.loadDayAtCourt(dayAtCourt);
            List<WebElement> slots = page.getAllTimeSlots();
            TimeTable timeTable = new TimeTable(slots, dayAtCourt);     // fixme: this step takes too long

            if (page.loggedInAsRegisteredUser()) {
                cache.addIfCacheable(dayAtCourt, timeTable.getAggregatedCourt());
            } else {
                timeTable.updateFromCache(cache);
            }

            if (timeTable.isOfferFound(cache)) {
                boolean firstFind = !foundAny;
                forcedRefresh.enableOnceFor(2).crawls();
                if (firstFind) audioPlayer.playSound();
                foundAny = true;
            }
            timeTable.printTable(cache);
        }
        if (!foundAny) forcedRefresh.disable();

        if (cacheStale) {
            cache.setUpdated();
        }

        Calendar.printCalendar(cache);
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

}
