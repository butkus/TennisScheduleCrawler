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

import static com.butkus.tenniscrawler.ExtensionInterest.ANY;
import static com.butkus.tenniscrawler.ExtensionInterest.EARLIER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;

/**
 * <h2>Note</h2>
 * DesireOrderPairer and DesireMaker have overlapping functionality. Here's why: <br/>
 * DesireOrderPairer validates if there's stray Orders, ambiguous pairing options, and so on. <br/>
 * DesireMaker validates for duplicate Desires, among doing other things. <br/>
 * If we had non-destructive inputs, we could take them and validate once at first step. <br/>
 * Perhaps we could do that, but instead, we have a 2-stage processing: <br/>
 * <ol>
 * <li>DesireMaker: cherry-pics and reduces input Desires to it's "effective" state, e.g. explicit desire supersedes a similar periodic one</li>
 * <li>DesireOrderPairer takes resulting Desires, combines them with Orders and performs a validation from pairing point of view</li>
 * </ol>
 * As a result, separate validation is required at both steps, because 2 wrongs can make a right in a corner case.
 * This may cause hard to debug bugs. Therefore, we want to ensure correct outputs at every step.
 *
 */
class DesireMakerTest {

    private DesireMaker desireMaker;
    private MockedStatic<DesiresExplicit> desiresExplicitMockedStatic;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(Instant.parse("2023-12-24T12:35:00.00Z"), ZoneId.of("Europe/Vilnius"));       // 2023-12-24 is Sunday
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
                new Desire("2023-12-31", ANY)); // sun

        List<Desire> actual = desireMaker
                .addExplicitDesires()
                .addNext(1, DayOfWeek.WEDNESDAY)
                .addNext(1, DayOfWeek.SUNDAY)   // not in order, but result will be
                .addNext(1, DayOfWeek.THURSDAY)
                .make();
        assertEquals(expected, actual);
    }

    @Nested
    class SameDay {

        @Test
        void exist_1Explicit_1Periodic_sameCategory_doesNotThrow_becauseExplicitOneIsSelected() {
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
        void exist_2_explicitDesires_HasSameCategory_throws() {
            List<Desire> explicitDesires = makeDesires(
                    new Desire(LocalDate.parse("2023-12-28"), ANY, Court.getIndoorIds()),
                    new Desire(LocalDate.parse("2023-12-28"), ANY, Court.getIndoorIds()));
            desiresExplicitMockedStatic.when(DesiresExplicit::makeExplicitDesires).thenReturn(explicitDesires);

            Executable makeExplicit = () -> desireMaker.addExplicitDesires().make();
            assertThrows(DuplicateDesiresException.class, makeExplicit);
        }

        // todo kinds should be better with 2 (1 explicit and 1 periodic) for consistency, but this one tests addNextInAndOut() as well
        @Test
        void exist_3PeriodicDesires_hasSameCategory_throws() {
            Executable periodicDesires = () -> desireMaker
                    .addNextInAndOut(1, DayOfWeek.THURSDAY)
                    .addNext(1, DayOfWeek.THURSDAY)
                    .make();
            assertThrows(DuplicateDesiresException.class, periodicDesires);
        }

        @Test
        void exist_2Periodic_2Explicit_explicitOnesAreSelected() {
            List<Desire> explicitDesires = makeDesires(
                    new Desire(LocalDate.parse("2023-12-28"), EARLIER, Court.getIndoorIds()),
                    new Desire(LocalDate.parse("2023-12-28"), EARLIER, Court.getOutdoorIds()));
            desiresExplicitMockedStatic.when(DesiresExplicit::makeExplicitDesires).thenReturn(explicitDesires);

            List<Desire> expected = new ArrayList<>(explicitDesires);

            List<Desire> actual = desireMaker
                    .addExplicitDesires()
                    .addNextInAndOut(1, DayOfWeek.THURSDAY)
                    .make();

            assertEquals(expected, actual);
        }

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

    private static List<Desire> makeDesires(Desire... desires) {
        return new ArrayList<>(Arrays.asList(desires));
    }

}