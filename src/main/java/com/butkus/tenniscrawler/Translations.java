package com.butkus.tenniscrawler;

import lombok.experimental.UtilityClass;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.Map;

@UtilityClass
public class Translations {

    private static final Map<Month, String> ltMonths = Map.ofEntries(
            Map.entry(Month.JANUARY, "Sausis"),
            Map.entry(Month.FEBRUARY, "Vasaris"),
            Map.entry(Month.MARCH, "Kovas"),
            Map.entry(Month.APRIL, "Balandis"),
            Map.entry(Month.MAY, "Gegužė"),
            Map.entry(Month.JUNE, "Birželis"),
            Map.entry(Month.JULY, "Liepa"),
            Map.entry(Month.AUGUST, "Rugpjūtis"),
            Map.entry(Month.SEPTEMBER, "Rugsėjis"),
            Map.entry(Month.OCTOBER, "Spalis"),
            Map.entry(Month.NOVEMBER, "Lapkritis"),
            Map.entry(Month.DECEMBER, "Gruodis"));

    private static final Map<DayOfWeek, String> ltWeekDays = Map.ofEntries(
            Map.entry(DayOfWeek.MONDAY,    "Pirma"),
            Map.entry(DayOfWeek.TUESDAY,   "Antra"),
            Map.entry(DayOfWeek.WEDNESDAY, "Trečia"),
            Map.entry(DayOfWeek.THURSDAY,  "Ketvirta"),
            Map.entry(DayOfWeek.FRIDAY,    "Penkta"),
            Map.entry(DayOfWeek.SATURDAY,  "Šešta"),
            Map.entry(DayOfWeek.SUNDAY,    "Sekma"));

    public static String getLtMonth(YearMonth yearMonth) {
        return ltMonths.get(yearMonth.getMonth());
    }

    public static String getLtWeekDay(LocalDate localDate) {
        return ltWeekDays.get(localDate.getDayOfWeek());
    }

}
