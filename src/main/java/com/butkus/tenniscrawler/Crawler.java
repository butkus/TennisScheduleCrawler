package com.butkus.tenniscrawler;

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

import static java.time.DayOfWeek.*;

@Component
public class Crawler {

    public static final Clock CLOCK = Clock.system(ZoneId.of("Europe/Vilnius"));
    private final Random random;
    private final Duration sleepUpTo;
    private final DesiresIteratorThingy desiresThingy;

    @Autowired
    public Crawler(@Value("${app.sleep-up-to}") Duration sleepUpTo,
                   DesiresIteratorThingy desiresThingy) throws NoSuchAlgorithmException {
        this.sleepUpTo = sleepUpTo;
        this.random = SecureRandom.getInstanceStrong();
        this.desiresThingy = desiresThingy;
    }

    @Scheduled(cron = "${app.cron}")
    public void run() throws InterruptedException {
        crawlEverythingOnce();
    }

    private void crawlEverythingOnce() throws InterruptedException {
        int sleepSeconds = random.nextInt((int) sleepUpTo.toSeconds());
        TimeUnit.SECONDS.sleep(sleepSeconds);
        Instant start = Instant.now();

        System.out.printf("-----------------------------------------------------------%n%nScan started at " + getTimeString(start) + "%n");
        newSystem();
        printCrawlEndTime(start);
    }

    private void printCrawlEndTime(Instant start) {
        Instant end = Instant.now();
        long jobLengthInSeconds = start.until(end, ChronoUnit.SECONDS);
        System.out.printf("Scan took %s seconds and finished at %s%n", jobLengthInSeconds, getTimeString(end));
    }

    private String getTimeString(Instant start) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(" HH:mm");
        LocalDateTime localDateTime = start.atZone(ZoneId.of("Europe/Vilnius")).toLocalDateTime();
        return localDateTime.format(formatter);
    }

    public void newSystem() {
        DesireMaker desireMaker = new DesireMaker(CLOCK);
        List<Desire> inputs = desireMaker
                .addExplicitDesires()
                .addNext(4, TUESDAY)
                .addNext(4, WEDNESDAY)
                .addNext(4, THURSDAY)
                .addNext(4, SUNDAY)
                .make();
        desiresThingy.doWork(inputs);
    }

}
