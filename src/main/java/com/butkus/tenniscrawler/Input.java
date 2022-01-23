package com.butkus.tenniscrawler;

import lombok.experimental.UtilityClass;
import org.javatuples.Triplet;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.butkus.tenniscrawler.Court.CARPET;
import static com.butkus.tenniscrawler.Court.HARD;
import static com.butkus.tenniscrawler.ExtensionInterest.*;

@UtilityClass
public class Input {

    public static List<Triplet<LocalDate, Integer, ExtensionInterest>> makeInputs() {
        List<Triplet<LocalDate, Integer, ExtensionInterest>> specialDays = new ArrayList<>();
        specialDays.addAll(getHolidays());
        specialDays.addAll(getExceptionDays());

        LocalDate date = LocalDate.now();
        List<Triplet<LocalDate, Integer, ExtensionInterest>> listHard = new ArrayList<>();
        List<Triplet<LocalDate, Integer, ExtensionInterest>> listCarpet = new ArrayList<>();
        for (int i=0; i<30; i++) {
            LocalDate currentDate = date.plusDays(i);
            boolean currentDateInSpecialDays = specialDays.stream().anyMatch(e -> e.getValue0().equals(currentDate));
            if (currentDateInSpecialDays) {
                listHard.addAll(getFrom(specialDays, currentDate, HARD));
                listCarpet.addAll(getFrom(specialDays, currentDate, CARPET));
            } else {
                listHard.add(Triplet.with(currentDate, HARD, ANY));
                listCarpet.add(Triplet.with(currentDate, CARPET, ANY));
            }
        }

//        listHard.add(Triplet.with(LocalDate.parse("2022-02-08"), HARD, EARLIER));
//        listCarpet.add(Triplet.with(LocalDate.parse("2022-02-08"), CARPET, ANY));
//        listHard.add(Triplet.with(LocalDate.parse("2022-02-26"), HARD, ANY));
//        listHard.add(Triplet.with(LocalDate.parse("2022-02-26"), CARPET, ANY));
        List<Triplet<LocalDate, Integer, ExtensionInterest>> list = new ArrayList<>();
        list.addAll(listHard);
        list.addAll(listCarpet);
        return list;
    }

    private static List<Triplet<LocalDate, Integer, ExtensionInterest>> getFrom(
            List<Triplet<LocalDate, Integer, ExtensionInterest>> specialDays, LocalDate date, int courtId) {
        return specialDays.stream()
                .filter(e -> e.getValue0().equals(date))
                .filter(e -> e.getValue1().equals(courtId))
                .collect(Collectors.toList());
    }

    private static List<Triplet<LocalDate, Integer, ExtensionInterest>> getHolidays() {

        int thisYear = LocalDate.now().getYear();
        int nextYear = thisYear + 1;

        List<Triplet<LocalDate, Integer, ExtensionInterest>> holidays = new ArrayList<>();
        addFixedHolidays(holidays, thisYear);
        addFixedHolidays(holidays, nextYear);

        return holidays;
    }

    private static void addFixedHolidays(List<Triplet<LocalDate, Integer, ExtensionInterest>> holidays, int year) {
        addExclusions(holidays, LocalDate.of(year, 1, 1));
        addExclusions(holidays, LocalDate.of(year, 2, 16));
        addExclusions(holidays, LocalDate.of(year, 3, 11));
        addExclusions(holidays, LocalDate.of(year, 5, 1));
        addExclusions(holidays, LocalDate.of(year, 6, 24));
        addExclusions(holidays, LocalDate.of(year, 7, 6));
        addExclusions(holidays, LocalDate.of(year, 8, 15));
        addExclusions(holidays, LocalDate.of(year, 11, 1));
        addExclusions(holidays, LocalDate.of(year, 11, 2));
        addExclusions(holidays, LocalDate.of(year, 12, 24));
        addExclusions(holidays, LocalDate.of(year, 12, 25));
        addExclusions(holidays, LocalDate.of(year, 12, 26));
    }

    private static List<Triplet<LocalDate, Integer, ExtensionInterest>> getExceptionDays() {
        List<Triplet<LocalDate, Integer, ExtensionInterest>> exceptionDays = new ArrayList<>();

        exceptionDays.add(Triplet.with(LocalDate.parse("2022-01-17"), HARD, LATER));        // fixme: If I don't add this, 2022-01-17 HARD won't be cached (will be skipped)
        exceptionDays.add(Triplet.with(LocalDate.parse("2022-01-17"), CARPET, LATER));      // fixme: but add this, and 2022-01-17 CARPET will say:  Requested LATER for date=2022-01-17 and court=Kilimas (courtId=8) but no existing booking

        addExclusions(exceptionDays, "2022-01-23");        // sunday, booked and happy with
        addExclusions(exceptionDays, "2022-01-24");        // monday, not interested
        addExclusions(exceptionDays, "2022-01-25");        // tuesday, 1830, happy with

        addExclusions(exceptionDays, "2022-01-27");        // thursday
        addExclusions(exceptionDays, "2022-01-28");     // dovile stand-by, already booked with Delfi
        addExclusions(exceptionDays, "2022-01-29");        // saturday

        exceptionDays.add(Triplet.with((LocalDate.parse("2022-02-01")), HARD, EARLIER));        // turim 1900
        exceptionDays.add(Triplet.with((LocalDate.parse("2022-02-01")), CARPET, EARLIER));      // turim 1900

        addExclusions(exceptionDays, "2022-02-04");        // friday, booked on delfi
        addExclusions(exceptionDays, "2022-02-05");        // saturday, not interested

        exceptionDays.add(Triplet.with((LocalDate.parse("2022-02-08")), HARD, EARLIER));        // turim 1900
        exceptionDays.add(Triplet.with((LocalDate.parse("2022-02-08")), CARPET, EARLIER));      // turim 1900

        exceptionDays.add(Triplet.with((LocalDate.parse("2022-02-09")), HARD, LATER));        // turim 18:30
        exceptionDays.add(Triplet.with((LocalDate.parse("2022-02-09")), CARPET, LATER));      // turim 18:30

        exceptionDays.add(Triplet.with(LocalDate.parse("2022-02-10"), HARD, EARLIER));          // turim 1930
        exceptionDays.add(Triplet.with(LocalDate.parse("2022-02-10"), CARPET, EARLIER));        // turim 1930

        addExclusions(exceptionDays, "2022-02-11");        // FRIDAY, BOOKED AT DELFI
        addExclusions(exceptionDays, "2022-02-12");        // saturday

        exceptionDays.add(Triplet.with(LocalDate.parse("2022-02-14"), HARD, EARLIER));          // turim 1930
        exceptionDays.add(Triplet.with(LocalDate.parse("2022-02-14"), CARPET, EARLIER));        // turim 1930

        exceptionDays.add(Triplet.with(LocalDate.parse("2022-02-15"), HARD, EARLIER));          // turim 1900
        exceptionDays.add(Triplet.with(LocalDate.parse("2022-02-15"), CARPET, EARLIER));        // turim 1900

        addExclusions(exceptionDays, "2022-02-18");        // friday, booked at delfi

        addExclusions(exceptionDays, "2022-02-19");        // saturday
        addExclusions(exceptionDays, "2022-02-26");        // saturday
        return exceptionDays;
    }

    private static void addExclusions(List<Triplet<LocalDate, Integer, ExtensionInterest>> result, String date) {
        result.add(Triplet.with(LocalDate.parse(date), HARD, NONE));
        result.add(Triplet.with(LocalDate.parse(date), CARPET, NONE));
    }
    private static void addExclusions(List<Triplet<LocalDate, Integer, ExtensionInterest>> result, LocalDate localDate) {
        result.add(Triplet.with(localDate, HARD, NONE));
        result.add(Triplet.with(localDate, CARPET, NONE));
    }
}
