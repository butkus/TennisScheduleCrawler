package com.butkus.tenniscrawler;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;

class DesireMakerTest {

    private DesireMaker desireMaker;
    private MockedStatic<DesiresExplicit> desiresExplicitMockedStatic;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(Instant.parse("2023-12-24T12:35:00.00Z"), ZoneId.of("Europe/Vilnius"));
        desireMaker = spy(new DesireMaker(clock));
        desiresExplicitMockedStatic = mockStatic(DesiresExplicit.class);
    }

    @AfterEach
    void tearDown() {
        desiresExplicitMockedStatic.close();
    }

    @Test
    void getNext1Thursday() {
        List<Desire> expected = new ArrayList<>();
        expected.add(new Desire("2023-12-28")); // thursday
        List<Desire> actual = desireMaker.addNext(1, DayOfWeek.THURSDAY).make();
        assertEquals(expected, actual);
    }

    @Test
    void getNext5Thursdays() {
        List<Desire> expected = new ArrayList<>();
        expected.add(new Desire("2023-12-28"));
        expected.add(new Desire("2024-01-04"));
        expected.add(new Desire("2024-01-11"));
        expected.add(new Desire("2024-01-18"));
        expected.add(new Desire("2024-01-25"));

        List<Desire> actual = desireMaker.addNext(5, DayOfWeek.THURSDAY).make();

        assertEquals(expected, actual);
    }

    @Test
    void explicitDesireHasPriority() {
        List<Desire> nextThursday = new ArrayList<>();
        nextThursday.add(new Desire("2023-12-28", ExtensionInterest.LATER));
        desiresExplicitMockedStatic.when(DesiresExplicit::makeExplicitDesires).thenReturn(nextThursday);

        List<Desire> expected = new ArrayList<>();
        expected.add(new Desire("2023-12-28", ExtensionInterest.LATER));    // LATER is to verify that explicit is taken; periodic would be ANY

        List<Desire> actualExplicitThenPeriodic = desireMaker.addExplicitDesires().addNext(1, DayOfWeek.THURSDAY).make();
        assertEquals(expected, actualExplicitThenPeriodic);

        desireMaker.reset();
        List<Desire> actualPeriodicThenExplicit = desireMaker.addNext(1, DayOfWeek.THURSDAY).addExplicitDesires().make();
        assertEquals(expected, actualPeriodicThenExplicit);
    }

    @Test
    void explicitWednesdayAndThursday_andImplicitWednesdayAndThursdayAndSunday_makes3DesiresSorted() {
        List<Desire> stubbedExplicit = new ArrayList<>();
        stubbedExplicit.add(new Desire("2023-12-27", ExtensionInterest.EARLIER)); // wed
        stubbedExplicit.add(new Desire("2023-12-28", ExtensionInterest.LATER)); // thu
        desiresExplicitMockedStatic.when(DesiresExplicit::makeExplicitDesires).thenReturn(stubbedExplicit);

        List<Desire> expected = new ArrayList<>();
        expected.add(new Desire("2023-12-27", ExtensionInterest.EARLIER)); // wed
        expected.add(new Desire("2023-12-28", ExtensionInterest.LATER)); // thu
        expected.add(new Desire("2023-12-31", ExtensionInterest.ANY)); // sun

        List<Desire> actual = desireMaker
                .addExplicitDesires()
                .addNext(1, DayOfWeek.WEDNESDAY)
                .addNext(1, DayOfWeek.SUNDAY)   // not in order, but result will be
                .addNext(1, DayOfWeek.THURSDAY)
                .make();
        assertEquals(expected, actual);
    }

    @Nested
    class Holidays {

        // todo in calendar: make holiday bookings visible that they're on holiday

        @Test
        void periodicDesiresSkipHolidayButExplicitDesiresDoNot() {
            // todo refactor, too many comments

            // Test explanation:
            // implicit + holiday = no desire: 2023-12-25
            // implicit + explicit + holiday = yes desire: 2023-12-26
            // explicit + holiday = yes desire: 2024-01-01
            desiresExplicitMockedStatic.when(DesiresExplicit::makeExplicitDesires)
                    .thenReturn(new ArrayList<>(List.of(new Desire("2023-12-26"), new Desire("2024-01-01"))));

            List<Desire> expected = List.of(new Desire("2023-12-26"), new Desire("2024-01-01"));
            List<Desire> actual = desireMaker.addExplicitDesires()
                    .addNext(1, DayOfWeek.MONDAY)   // clock=2023-12-24 --> next monday = 2023-12-25 First Christmas day (only 1 monday requested, so 2024-01-01 is not added)
                    .addNext(1, DayOfWeek.TUESDAY)  // clock=2023-12-24 --> next tuesday = 2023-12-26 Second Christmas day
                    .make();
            assertEquals(expected, actual);
        }
    }

}