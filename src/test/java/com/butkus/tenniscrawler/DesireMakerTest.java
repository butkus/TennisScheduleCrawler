package com.butkus.tenniscrawler;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.MockedStatic;

import java.time.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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
        List<Desire> expected = makeDesires(new Desire("2023-12-28")); // thursday
        List<Desire> actual = desireMaker.addNext(1, DayOfWeek.THURSDAY).make();
        assertEquals(expected, actual);
        assertFalse(actual.get(0).isUseRedundantBooking());
    }

    @Test
    void getNext1ThursdayRedundant() {
        List<Desire> expected = makeDesires(new Desire(LocalDate.parse("2023-12-28"), Court.getIndoorIds(), Court.getOutdoorIds()));
        List<Desire> actual = desireMaker.addNextRedundant(1, DayOfWeek.THURSDAY, Court.getIndoorIds(), Court.getOutdoorIds()).make();
        assertEquals(expected, actual);
        assertTrue(actual.get(0).isUseRedundantBooking());
    }

    // todo remove redundant desire infra
    // fixme: This tests for duplicated desires, but so does DesireOrderPairerTest
    //   DesireOrderPairerTest is more dedicated for this purpose. Remove this below (and related) test from here?
    @Test
    void exist_3_periodicDesireForSameDay_shouldThrow() {
        Executable regularDesires = () -> desireMaker
                .addNext(1, DayOfWeek.THURSDAY)
                .addNext(1, DayOfWeek.THURSDAY)
                .addNext(1, DayOfWeek.THURSDAY)
                .make();
        assertThrows(DuplicateDesiresException.class, regularDesires);

        desireMaker.reset();

        Executable redundantDesires = () -> desireMaker
                .addNextRedundant(1, DayOfWeek.THURSDAY, Court.getIndoorIds(), Court.getOutdoorIds())
                .addNextRedundant(1, DayOfWeek.THURSDAY, Court.getGrassIds(), Court.getCarpetIds())
                .addNextRedundant(1, DayOfWeek.THURSDAY, Court.getClayIds(), Court.getHardIds())
                .make();
        assertThrows(DuplicateDesiresException.class, redundantDesires);

        desireMaker.reset();

        Executable mixedDesires = () -> desireMaker
                .addNextRedundant(1, DayOfWeek.THURSDAY, Court.getIndoorIds(), Court.getOutdoorIds())
                .addNext(1, DayOfWeek.THURSDAY)
                .addNext(1, DayOfWeek.THURSDAY)
                .make();
        assertThrows(DuplicateDesiresException.class, mixedDesires);
    }

    @Test
    void exist_3_explicitDesireForSameDay_shouldThrow() {
        List<Desire> regularDesires = makeDesires(new Desire("2023-12-28"), new Desire("2023-12-28"), new Desire("2023-12-28"));
        desiresExplicitMockedStatic.when(DesiresExplicit::makeExplicitDesires).thenReturn(regularDesires);
        Executable makeExplicitDesires = () -> desireMaker.addExplicitDesires().make();
        assertThrows(DuplicateDesiresException.class, makeExplicitDesires);

        desireMaker.reset();

        List<Desire> redundantDesires = makeDesires(
                new Desire(LocalDate.parse("2023-12-28"), Court.getIndoorIds(), Court.getOutdoorIds()),
                new Desire(LocalDate.parse("2023-12-28"), Court.getIndoorIds(), Court.getOutdoorIds()),
                new Desire(LocalDate.parse("2023-12-28"), Court.getClayIds(), Court.getGrassIds()));
        desiresExplicitMockedStatic.when(DesiresExplicit::makeExplicitDesires).thenReturn(redundantDesires);
        assertThrows(DuplicateDesiresException.class, makeExplicitDesires);

        desireMaker.reset();

        List<Desire> mixedDesires = makeDesires(
                new Desire(LocalDate.parse("2023-12-28"), Court.getIndoorIds(), Court.getOutdoorIds()),
                new Desire(LocalDate.parse("2023-12-28"), Court.getIndoorIds(), Court.getOutdoorIds()),
                new Desire("2023-12-28"));
        desiresExplicitMockedStatic.when(DesiresExplicit::makeExplicitDesires).thenReturn(mixedDesires);
        assertThrows(DuplicateDesiresException.class, makeExplicitDesires);
    }

    @Test
    void exist_2_desiresForSameDay_shouldNotThrow() {
        Executable makePeriodicDesires = () -> desireMaker
                .addNext(1, DayOfWeek.THURSDAY)
                .addNext(1, DayOfWeek.THURSDAY)
                .make();
        assertDoesNotThrow(makePeriodicDesires);

        desireMaker.reset();

        List<Desire> explicitDesires = makeDesires(new Desire("2023-12-28"), new Desire("2023-12-28"));
        desiresExplicitMockedStatic.when(DesiresExplicit::makeExplicitDesires).thenReturn(explicitDesires);
        Executable makeExplicitDesires = () -> desireMaker.addExplicitDesires().make();
        assertDoesNotThrow(makeExplicitDesires);
    }

    private static List<Desire> makeDesires(Desire... desires) {
        return new ArrayList<>(Arrays.asList(desires));
    }

    @Test
    void getNext5Thursdays() {
        List<Desire> expected = makeDesires(
                new Desire("2023-12-28"),
                new Desire("2024-01-04"),
                new Desire("2024-01-11"),
                new Desire("2024-01-18"),
                new Desire("2024-01-25"));
        List<Desire> actual = desireMaker.addNext(5, DayOfWeek.THURSDAY).make();

        assertEquals(expected, actual);
        assertTrue(actual.stream().noneMatch(Desire::isUseRedundantBooking));
    }

    @Test
    void explicitDesireHasPriority() {
        List<Desire> nextThursday = makeDesires(new Desire("2023-12-28", ExtensionInterest.LATER));
        desiresExplicitMockedStatic.when(DesiresExplicit::makeExplicitDesires).thenReturn(nextThursday);

        // LATER is to verify that explicit is taken; periodic would be ANY
        List<Desire> expected = makeDesires(new Desire("2023-12-28", ExtensionInterest.LATER));

        List<Desire> actualExplicitThenPeriodic = desireMaker.addExplicitDesires().addNext(1, DayOfWeek.THURSDAY).make();
        assertEquals(expected, actualExplicitThenPeriodic);

        desireMaker.reset();
        List<Desire> actualPeriodicThenExplicit = desireMaker.addNext(1, DayOfWeek.THURSDAY).addExplicitDesires().make();
        assertEquals(expected, actualPeriodicThenExplicit);
    }

    @Test
    void explicitWednesdayAndThursday_andImplicitWednesdayAndThursdayAndSunday_makes3DesiresSorted() {
        List<Desire> stubbedExplicit = makeDesires(
                new Desire("2023-12-27", ExtensionInterest.EARLIER), // wed
                new Desire("2023-12-28", ExtensionInterest.LATER)); // thu
        desiresExplicitMockedStatic.when(DesiresExplicit::makeExplicitDesires).thenReturn(stubbedExplicit);

        List<Desire> expected = makeDesires(
                new Desire("2023-12-27", ExtensionInterest.EARLIER), // wed
                new Desire("2023-12-28", ExtensionInterest.LATER), // thu
                new Desire("2023-12-31", ExtensionInterest.ANY)); // sun

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