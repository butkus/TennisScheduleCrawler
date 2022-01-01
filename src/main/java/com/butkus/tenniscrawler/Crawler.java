package com.butkus.tenniscrawler;

import org.javatuples.Pair;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Component
public class Crawler {

    public static final int HARD = 2;
    public static final int CARPET = 8;

    private final AudioPlayer audioPlayer;
    private final Page page;

    @Autowired
    public Crawler(AudioPlayer audioPlayer, Page page) {
        this.audioPlayer = audioPlayer;
        this.page = page;
    }

    @Scheduled(cron = "${app.cron}")
    public void run() {
        Instant start = Instant.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd EEEE,  HH:mm:ss");
        LocalDateTime localDateTime = start.atZone(ZoneId.of("Europe/Vilnius")).toLocalDateTime();
        String dateTime = localDateTime.format(formatter);
        System.out.printf("-- Crawl started at %s%n", dateTime);

        try {
            crawlAllOnce(page);
        } finally {
            page.close();
        }
        System.out.printf("-- Crawl took %s seconds%n", start.until(Instant.now(), ChronoUnit.SECONDS));
    }

    private void crawlAllOnce(Page page) {

        // 0)  18:00-18:30
        // 1)  18:30-19:00
        // 2)  19:00-19:30
        // 3)  19:30-20:00
        // 4)  20:00-20:30
        // 5)  20:30-21:00
        List<Pair<LocalDate, Integer>> inputs = makeInputs();
        boolean foundAny = false;

        for (Pair<LocalDate, Integer> pair : inputs) {
            String date = pair.getValue0().toString();
            Integer placeId = pair.getValue1();
            page.get(String.format("https://savitarna.tenisopasaulis.lt/rezervavimas/rezervavimas?sDate=%s&iPlaceId=%s", date, placeId));

            List<WebElement> slots = getAllTimeSlotsSeb(page);
            TimeTable timeTable = new TimeTable(slots, date, placeId);

            boolean foundInCurrent = false;
            if (timeTable.isOfferFound()) {
                foundAny = true;
                foundInCurrent = true;
            }

            String foundNotFoundMark = foundInCurrent ? "‹✔›" : " \uD83D\uDFA8 ";   // IntelliJ UTF-8 console output issue: https://stackoverflow.com/a/56430344
            System.out.printf("%-54s %s%n", timeTable.getReadableAggregatedCourt(), foundNotFoundMark);     // fixme replace arbitrary 54
        }

        if (foundAny) audioPlayer.playSound();
    }

    private static List<Pair<LocalDate, Integer>> makeInputs() {
        List<LocalDate> excludedDays = new ArrayList<>();
        excludedDays.addAll(getHolidays());
        excludedDays.addAll(getUnwantedDays());

        LocalDate date = LocalDate.now();
        List<Pair<LocalDate, Integer>> listHard = new ArrayList<>();
        List<Pair<LocalDate, Integer>> listCarpet = new ArrayList<>();
//        for (int i=0; i<25; i++) {
//            LocalDate currentDate = date.plusDays(i);
//            if (excludedDays.contains(currentDate)) continue;
//            Pair<LocalDate, Integer> pairHard = Pair.with(currentDate, HARD);
//            Pair<LocalDate, Integer> pairCarpet = Pair.with(currentDate, CARPET);
//            listHard.add(pairHard);
//            listCarpet.add(pairCarpet);
//        }
//        listHard.add(Pair.with(LocalDate.parse("2022-01-05"), HARD));
//        listHard.add(Pair.with(LocalDate.parse("2022-01-06"), HARD));
        listHard.add(Pair.with(LocalDate.parse("2022-01-09"), HARD));           // needs better time
//
//        listHard.add(Pair.with(LocalDate.parse("2022-01-16"), HARD));
//        listHard.add(Pair.with(LocalDate.parse("2022-01-30"), HARD));
//
//        listCarpet.add(Pair.with(LocalDate.parse("2022-01-05"), CARPET));
//        listCarpet.add(Pair.with(LocalDate.parse("2022-01-06"), CARPET));
//        listCarpet.add(Pair.with(LocalDate.parse("2022-01-09"), CARPET));       // needs better time
//
//        listCarpet.add(Pair.with(LocalDate.parse("2022-01-16"), CARPET));
//        listCarpet.add(Pair.with(LocalDate.parse("2022-01-30"), CARPET));
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
        result.add(LocalDate.parse("2021-12-30"));
        result.add(LocalDate.parse("2021-12-31"));
        result.add(LocalDate.parse("2022-01-02"));

        return result;
    }

    private static List<WebElement> getAllTimeSlotsSeb(Page page) {
        return page.findElements(By.id("jqReservationLink"));
    }

}
