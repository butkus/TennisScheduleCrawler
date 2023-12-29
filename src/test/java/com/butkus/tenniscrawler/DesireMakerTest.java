package com.butkus.tenniscrawler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

class DesireMakerTest {

    private Clock clock;
    private DesireMaker desireMaker;

    @BeforeEach
    void setUp() {
        this.clock = Clock.fixed(Instant.parse("2023-10-15T12:35:00.00Z"), ZoneId.of("Europe/Vilnius"));
        this.desireMaker = spy(new DesireMaker(clock));
    }

    @Test
    void getNext1Thursday() {
        List<Desire> expected = new ArrayList<>();
        expected.add(new Desire("2023-10-19"));
        List<Desire> actual = desireMaker.addNext(1, DayOfWeek.THURSDAY).make();
        assertEquals(expected, actual);
    }

    @Test
    void getNext5Thursdays() {
        List<Desire> expected = new ArrayList<>();
        expected.add(new Desire("2023-10-19"));
        expected.add(new Desire("2023-10-26"));
        expected.add(new Desire("2023-11-02"));
        expected.add(new Desire("2023-11-09"));
        expected.add(new Desire("2023-11-16"));

        List<Desire> actual = desireMaker.addNext(5, DayOfWeek.THURSDAY).make();

        assertEquals(expected, actual);
    }

    @Test
    void explicitDesireHasPriority() {
        List<Desire> nextThursday = new ArrayList<>();
        nextThursday.add(new Desire("2023-10-19", ExtensionInterest.LATER));
        doReturn(nextThursday).when(desireMaker).makeExplicitDesires();

        List<Desire> expected = new ArrayList<>();
        expected.add(new Desire("2023-10-19", ExtensionInterest.LATER));

        List<Desire> actualExplicitThenPeriodic = desireMaker.addExplicitDesires().addNext(1, DayOfWeek.THURSDAY).make();
        assertEquals(expected, actualExplicitThenPeriodic);

        desireMaker.reset();
        List<Desire> actualPeriodicThenExplicit = desireMaker.addNext(1, DayOfWeek.THURSDAY).addExplicitDesires().make();
        assertEquals(expected, actualPeriodicThenExplicit);
    }

    @Test
    void explicitTuesdayAndThursdayAndImplicitTuesdayAndThursdayAndSunday_makes3DesiresSorted() {
        List<Desire> stubbedExplicit = new ArrayList<>();
        stubbedExplicit.add(new Desire("2023-10-17", ExtensionInterest.EARLIER)); // tue
        stubbedExplicit.add(new Desire("2023-10-19", ExtensionInterest.LATER)); // thu
        doReturn(stubbedExplicit).when(desireMaker).makeExplicitDesires();

        List<Desire> expected = new ArrayList<>();
        expected.add(new Desire("2023-10-17", ExtensionInterest.EARLIER)); // tue
        expected.add(new Desire("2023-10-19", ExtensionInterest.LATER)); // thu
        expected.add(new Desire("2023-10-22", ExtensionInterest.ANY)); // sun

        List<Desire> actual = desireMaker
                .addExplicitDesires()
                .addNext(1, DayOfWeek.TUESDAY)
                .addNext(1, DayOfWeek.SUNDAY)   // not in order, but result will be
                .addNext(1, DayOfWeek.THURSDAY)
                .make();
        assertEquals(expected, actual);
    }

}