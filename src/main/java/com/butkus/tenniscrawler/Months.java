package com.butkus.tenniscrawler;

import lombok.Getter;
import lombok.experimental.UtilityClass;

import java.time.Month;
import java.util.Map;

@UtilityClass
public class Months {

    @Getter
    public static final Map<Month, String> ltMonths = Map.ofEntries(
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

}
