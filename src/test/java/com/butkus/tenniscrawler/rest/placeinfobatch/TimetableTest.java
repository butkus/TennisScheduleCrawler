package com.butkus.tenniscrawler.rest.placeinfobatch;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TimetableTest {

    public static final String OCCUPIED = "full";
    public static final String FULL_SELL = "fullsell";
    public static final String FREE = "free";

    Timetable timetable;

    @ParameterizedTest
    @CsvSource({FREE, FULL_SELL})
    void simple30minSlotWorks(String sellable) {
        timetable = new Timetable();
        timetable.setT1730(new HalfHour(LocalTime.parse("17:30"), LocalTime.parse("18:00"), OCCUPIED));
        timetable.setT1800(new HalfHour(LocalTime.parse("18:00"), LocalTime.parse("18:30"), sellable));
        timetable.setT1830(new HalfHour(LocalTime.parse("18:30"), LocalTime.parse("19:00"), OCCUPIED));

        boolean actual = timetable.hasVacanciesExtended(LocalTime.parse("18:00"), LocalTime.parse("18:30"));
        assertTrue(actual);
    }

    @ParameterizedTest
    @CsvSource({FREE, FULL_SELL})
    void simple60minSlotWorks(String sellable) {
        timetable = new Timetable();
        timetable.setT1730(new HalfHour(LocalTime.parse("17:30"), LocalTime.parse("18:00"), OCCUPIED));
        timetable.setT1800(new HalfHour(LocalTime.parse("18:00"), LocalTime.parse("18:30"), sellable));
        timetable.setT1830(new HalfHour(LocalTime.parse("18:30"), LocalTime.parse("19:00"), sellable));
        timetable.setT1900(new HalfHour(LocalTime.parse("19:00"), LocalTime.parse("19:30"), OCCUPIED));

        boolean actual = timetable.hasVacanciesExtended(LocalTime.parse("18:00"), LocalTime.parse("19:00"));
        assertTrue(actual);
    }

    @ParameterizedTest
    @CsvSource({FREE, FULL_SELL})
    void want60butHave90_isOk(String sellable) {
        timetable = new Timetable();
        timetable.setT1730(new HalfHour(LocalTime.parse("17:30"), LocalTime.parse("18:00"), OCCUPIED));
        timetable.setT1800(new HalfHour(LocalTime.parse("18:00"), LocalTime.parse("18:30"), sellable));
        timetable.setT1830(new HalfHour(LocalTime.parse("18:30"), LocalTime.parse("19:00"), sellable));
        timetable.setT1900(new HalfHour(LocalTime.parse("19:00"), LocalTime.parse("19:30"), sellable));
        timetable.setT1930(new HalfHour(LocalTime.parse("19:30"), LocalTime.parse("20:00"), OCCUPIED));

        boolean actual = timetable.hasVacanciesExtended(LocalTime.parse("18:00"), LocalTime.parse("19:00"));
        assertTrue(actual);
    }

    @ParameterizedTest
    @CsvSource({FREE, FULL_SELL})
    void simple90minSlotWorks(String sellable) {
        timetable = new Timetable();
        timetable.setT1730(new HalfHour(LocalTime.parse("17:30"), LocalTime.parse("18:00"), OCCUPIED));
        timetable.setT1800(new HalfHour(LocalTime.parse("18:00"), LocalTime.parse("18:30"), sellable));
        timetable.setT1830(new HalfHour(LocalTime.parse("18:30"), LocalTime.parse("19:00"), sellable));
        timetable.setT1900(new HalfHour(LocalTime.parse("19:00"), LocalTime.parse("19:30"), sellable));
        timetable.setT1930(new HalfHour(LocalTime.parse("19:30"), LocalTime.parse("20:00"), OCCUPIED));

        boolean actual = timetable.hasVacanciesExtended(LocalTime.parse("18:00"), LocalTime.parse("19:30"));
        assertTrue(actual);
    }

    @ParameterizedTest
    @CsvSource({FREE, FULL_SELL})
    void want90minButHave60(String sellable) {
        timetable = new Timetable();
        timetable.setT1730(new HalfHour(LocalTime.parse("17:30"), LocalTime.parse("18:00"), OCCUPIED));
        timetable.setT1800(new HalfHour(LocalTime.parse("18:00"), LocalTime.parse("18:30"), sellable));
        timetable.setT1830(new HalfHour(LocalTime.parse("18:30"), LocalTime.parse("19:00"), sellable));
        timetable.setT1900(new HalfHour(LocalTime.parse("19:00"), LocalTime.parse("19:30"), OCCUPIED));
        timetable.setT1930(new HalfHour(LocalTime.parse("19:30"), LocalTime.parse("20:00"), OCCUPIED));

        boolean actual = timetable.hasVacanciesExtended(LocalTime.parse("18:00"), LocalTime.parse("19:30"));
        assertFalse(actual);
    }

    @ParameterizedTest
    @CsvSource({FREE, FULL_SELL})
    void want90minButIsInterrupted(String sellable) {
        timetable = new Timetable();
        timetable.setT1730(new HalfHour(LocalTime.parse("17:30"), LocalTime.parse("18:00"), OCCUPIED));
        timetable.setT1800(new HalfHour(LocalTime.parse("18:00"), LocalTime.parse("18:30"), sellable));
        timetable.setT1830(new HalfHour(LocalTime.parse("18:30"), LocalTime.parse("19:00"), sellable));
        timetable.setT1900(new HalfHour(LocalTime.parse("19:00"), LocalTime.parse("19:30"), OCCUPIED));
        timetable.setT1930(new HalfHour(LocalTime.parse("19:30"), LocalTime.parse("20:00"), sellable));

        boolean actual = timetable.hasVacanciesExtended(LocalTime.parse("18:00"), LocalTime.parse("19:30"));
        assertFalse(actual);
    }
}