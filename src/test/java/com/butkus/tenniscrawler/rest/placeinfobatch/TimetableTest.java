package com.butkus.tenniscrawler.rest.placeinfobatch;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TimetableTest {

    public static final String OCCUPIED = "full";
    public static final String FREE = "free";

    Timetable timetable;

    @Test
    void simple30minSlotWorks() {
        timetable = new Timetable();
        timetable.setT1730(new HalfHour(LocalTime.parse("17:30"), LocalTime.parse("18:00"), OCCUPIED));
        timetable.setT1800(new HalfHour(LocalTime.parse("18:00"), LocalTime.parse("18:30"), FREE)); // free
        timetable.setT1830(new HalfHour(LocalTime.parse("18:30"), LocalTime.parse("19:00"), OCCUPIED));

        boolean actual = timetable.hasVacanciesExtended(LocalTime.parse("18:00"), LocalTime.parse("18:30"));
        assertTrue(actual);
    }

    @Test
    void simple60minSlotWorks() {
        timetable = new Timetable();
        timetable.setT1730(new HalfHour(LocalTime.parse("17:30"), LocalTime.parse("18:00"), OCCUPIED));
        timetable.setT1800(new HalfHour(LocalTime.parse("18:00"), LocalTime.parse("18:30"), FREE)); // free
        timetable.setT1830(new HalfHour(LocalTime.parse("18:30"), LocalTime.parse("19:00"), FREE)); // free
        timetable.setT1900(new HalfHour(LocalTime.parse("19:00"), LocalTime.parse("19:30"), OCCUPIED));

        boolean actual = timetable.hasVacanciesExtended(LocalTime.parse("18:00"), LocalTime.parse("19:00"));
        assertTrue(actual);
    }

    @Test
    void want60butHave90_isOk() {
        timetable = new Timetable();
        timetable.setT1730(new HalfHour(LocalTime.parse("17:30"), LocalTime.parse("18:00"), OCCUPIED));
        timetable.setT1800(new HalfHour(LocalTime.parse("18:00"), LocalTime.parse("18:30"), FREE)); // free
        timetable.setT1830(new HalfHour(LocalTime.parse("18:30"), LocalTime.parse("19:00"), FREE)); // free
        timetable.setT1900(new HalfHour(LocalTime.parse("19:00"), LocalTime.parse("19:30"), FREE)); // free
        timetable.setT1930(new HalfHour(LocalTime.parse("19:30"), LocalTime.parse("20:00"), OCCUPIED));

        boolean actual = timetable.hasVacanciesExtended(LocalTime.parse("18:00"), LocalTime.parse("19:00"));
        assertTrue(actual);
    }
    @Test
    void simple90minSlotWorks() {
        timetable = new Timetable();
        timetable.setT1730(new HalfHour(LocalTime.parse("17:30"), LocalTime.parse("18:00"), OCCUPIED));
        timetable.setT1800(new HalfHour(LocalTime.parse("18:00"), LocalTime.parse("18:30"), FREE)); // free
        timetable.setT1830(new HalfHour(LocalTime.parse("18:30"), LocalTime.parse("19:00"), FREE)); // free
        timetable.setT1900(new HalfHour(LocalTime.parse("19:00"), LocalTime.parse("19:30"), FREE)); // free
        timetable.setT1930(new HalfHour(LocalTime.parse("19:30"), LocalTime.parse("20:00"), OCCUPIED));

        boolean actual = timetable.hasVacanciesExtended(LocalTime.parse("18:00"), LocalTime.parse("19:30"));
        assertTrue(actual);
    }

    @Test
    void want90minButHave60() {
        timetable = new Timetable();
        timetable.setT1730(new HalfHour(LocalTime.parse("17:30"), LocalTime.parse("18:00"), OCCUPIED));
        timetable.setT1800(new HalfHour(LocalTime.parse("18:00"), LocalTime.parse("18:30"), FREE)); // free
        timetable.setT1830(new HalfHour(LocalTime.parse("18:30"), LocalTime.parse("19:00"), FREE)); // free
        timetable.setT1900(new HalfHour(LocalTime.parse("19:00"), LocalTime.parse("19:30"), OCCUPIED));
        timetable.setT1930(new HalfHour(LocalTime.parse("19:30"), LocalTime.parse("20:00"), OCCUPIED));

        boolean actual = timetable.hasVacanciesExtended(LocalTime.parse("18:00"), LocalTime.parse("19:30"));
        assertFalse(actual);
    }

    @Test
    void want90minButIsInterrupted() {
        timetable = new Timetable();
        timetable.setT1730(new HalfHour(LocalTime.parse("17:30"), LocalTime.parse("18:00"), OCCUPIED));
        timetable.setT1800(new HalfHour(LocalTime.parse("18:00"), LocalTime.parse("18:30"), FREE)); // free
        timetable.setT1830(new HalfHour(LocalTime.parse("18:30"), LocalTime.parse("19:00"), FREE)); // free
        timetable.setT1900(new HalfHour(LocalTime.parse("19:00"), LocalTime.parse("19:30"), OCCUPIED));
        timetable.setT1930(new HalfHour(LocalTime.parse("19:30"), LocalTime.parse("20:00"), FREE)); // free

        boolean actual = timetable.hasVacanciesExtended(LocalTime.parse("18:00"), LocalTime.parse("19:30"));
        assertFalse(actual);
    }
}