package com.butkus.tenniscrawler;

import lombok.experimental.UtilityClass;
import org.javatuples.Triplet;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.butkus.tenniscrawler.Court.*;
import static com.butkus.tenniscrawler.ExtensionInterest.*;

// todo go through todo's, take what's useful and delete this class
@UtilityClass
public class Input {    // todo we now have DesiresIteratorThingy and probably other less-than-perfect names. So after deleting this Input class (and other legecy classes), do some refacting and maybe rename some new classes with old names. Is input better than Desire? I don't know. But some other legacy names might be better than new ones.

    public static List<Triplet<LocalDate, Integer, ExtensionInterest>> makeInputs() {
        List<Triplet<LocalDate, Integer, ExtensionInterest>> specialDays = new ArrayList<>();
        specialDays.addAll(getHolidays());
        specialDays.addAll(getExceptionDays());

        // todo 'Court' and 'Integer' refer to the same thing
        // Map<court type, list of extension preference for the day>
        Map<Court, List<Triplet<LocalDate, Integer, ExtensionInterest>>> inputMap = new EnumMap<>(Court.class);
        for (Court court : Court.values()) {
            List<Triplet<LocalDate, Integer, ExtensionInterest>> courtList = new ArrayList<>();
            inputMap.put(court, courtList);
        }

        LocalDate date = LocalDate.now();
        for (int i=0; i<24; i++) {
            LocalDate currentDate = date.plusDays(i);
            boolean currentDateInSpecialDays = specialDays.stream().anyMatch(e -> e.getValue0().equals(currentDate));
            if (currentDateInSpecialDays) {
                for (Court court : Court.values()) {
                    inputMap.get(court).addAll(getFrom(specialDays, currentDate, court.getCourtId()));
                }
            } else {
                for (Court court : Court.values()) {
                    inputMap.get(court).add(Triplet.with(currentDate, court.getCourtId(), ANY));
                }
            }
        }

        List<Triplet<LocalDate, Integer, ExtensionInterest>> list = new ArrayList<>();
        for (List<Triplet<LocalDate, Integer, ExtensionInterest>> courtList : inputMap.values()) {
            list.addAll(courtList);
        }
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
        for (Court court : Court.values()) {
            result.add(Triplet.with(LocalDate.parse(date), court.getCourtId(), NONE));
        }
    }
    private static void addExclusions(List<Triplet<LocalDate, Integer, ExtensionInterest>> result, LocalDate localDate) {
        for (Court court : Court.values()) {
            result.add(Triplet.with(localDate, court.getCourtId(), NONE));
        }
    }
}
