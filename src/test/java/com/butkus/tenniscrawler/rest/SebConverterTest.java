package com.butkus.tenniscrawler.rest;

import com.butkus.tenniscrawler.Court;
import com.butkus.tenniscrawler.rest.orders.Order;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SebConverterTest {

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class SameDay {

        @MethodSource("adjacent2SourceRandomOrder")
        @ParameterizedTest
        void adjacent2_coalesces(List<Order> inputs) {
            List<Order> actual = SebOrderConverter.coalesce(inputs);
            List<Order> expected = List.of(makeOrder("18:00", "19:30"));
            assertEquals(expected, actual);
        }
        Stream<Arguments> adjacent2SourceRandomOrder() {
            return Stream.of(
                    Arguments.of(new ArrayList<>(Arrays.asList(makeOrder("18:00", "19:00"), makeOrder("19:00", "19:30")))),
                    Arguments.of(new ArrayList<>(Arrays.asList(makeOrder("19:00", "19:30"), makeOrder("18:00", "19:00"))))
            );
        }


        @MethodSource("adjacent2And1DistantRandomOrder")
        @ParameterizedTest
        void adjacent2And1distant_coalesces2AndLeaves1asIs(List<Order> inputs) {
            List<Order> expected = List.of(makeOrder("18:00", "19:30"), makeOrder("20:30", "21:00"));
            List<Order> actual = SebOrderConverter.coalesce(inputs);
            assertEquals(expected, actual);
        }
        Stream<Arguments> adjacent2And1DistantRandomOrder() {
            return Stream.of(
                    Arguments.of(new ArrayList<>(Arrays.asList(makeOrder("18:00", "19:00"), makeOrder("19:00", "19:30"), makeOrder("20:30", "21:00")))),
                    Arguments.of(new ArrayList<>(Arrays.asList(makeOrder("18:00", "19:00"), makeOrder("20:30", "21:00"), makeOrder("19:00", "19:30")))),
                    Arguments.of(new ArrayList<>(Arrays.asList(makeOrder("20:30", "21:00"), makeOrder("19:00", "19:30"), makeOrder("18:00", "19:00"))))
            );
        }


        @Test
        void coalesces3AdjacentOnes() {
            Order from1800to1900 = makeOrder("18:00", "19:00");
            Order from1900to1930 = makeOrder("19:00", "19:30");
            Order from1930to2000 = makeOrder("19:30", "20:00");
            List<Order> inputs = new ArrayList<>(Arrays.asList(from1800to1900, from1900to1930, from1930to2000));

            Order from1800to2000 = makeOrder("18:00", "20:00");
            List<Order> expected = List.of(from1800to2000);

            List<Order> actual = SebOrderConverter.coalesce(inputs);
            assertEquals(expected, actual);
        }

        @Test
        void theresAGap_doesNotCoalesce() {
            Order from1800to1900 = makeOrder("18:00", "19:00");
            Order from2000to2100 = makeOrder("20:00", "21:00");
            List<Order> inputs = new ArrayList<>(List.of(from1800to1900, from2000to2100));

            List<Order> expected = List.of(from1800to1900.clone(), from2000to2100.clone());

            List<Order> actual = SebOrderConverter.coalesce(inputs);
            assertEquals(expected, actual);
        }

    }

    @Nested
    class DifferentDays {

        @Test
        void adjacent2ForEach2Days_coalescesInto2() {
            Order today18hTill19h = makeOrder("2023-09-17", "18:00", "19:00");
            Order today19hTill20h = makeOrder("2023-09-17", "19:00", "20:00");
            Order tomorrow18hTill19h = makeOrder("2023-09-18", "18:00", "19:00");
            Order tomorrow19hTill20h = makeOrder("2023-09-18", "19:00", "20:00");
            List<Order> inputs = new ArrayList<>(List.of(today18hTill19h, today19hTill20h, tomorrow18hTill19h, tomorrow19hTill20h));

            Order expectedToday18hTill20h = makeOrder("2023-09-17", "18:00", "20:00");
            Order expectedTomorrow18hTill20h = makeOrder("2023-09-18", "18:00", "20:00");
            List<Order> expected = List.of(expectedToday18hTill20h, expectedTomorrow18hTill20h);

            List<Order> actual = SebOrderConverter.coalesce(inputs);
            assertEquals(expected, actual);
        }

        @Test
        void inSequenceButDifferentDays_coalescesForSameDay() {
            Order today18hTill19h = makeOrder("2023-09-17", "18:00", "19:00");
            Order today19hTill20h = makeOrder("2023-09-17", "19:00", "20:00");
            Order tomorrow18hTill19h = makeOrder("2023-09-18", "20:00", "21:00");
            Order tomorrow19hTill20h = makeOrder("2023-09-18", "21:00", "22:00");
            List<Order> inputs = new ArrayList<>(List.of(today18hTill19h, today19hTill20h, tomorrow18hTill19h, tomorrow19hTill20h));

            Order expectedToday18hTill20h = makeOrder("2023-09-17", "18:00", "20:00");
            Order expectedTomorrow18hTill20h = makeOrder("2023-09-18", "20:00", "22:00");
            List<Order> expected = List.of(expectedToday18hTill20h, expectedTomorrow18hTill20h);

            List<Order> actual = SebOrderConverter.coalesce(inputs);
            assertEquals(expected, actual);
        }

    }

    @Nested
    class DifferentCourts {
        @Test
        void adjacent2For2Courts_coalescesInto2() {
            Order hard1from18hTill19h = makeOrder(Court.H01, "18:00", "19:00");
            Order hard1from19hTill20h = makeOrder(Court.H01, "19:00", "20:00");
            Order hard2from18hTill19h = makeOrder(Court.H02, "18:00", "19:00");
            Order hard2from19hTill20h = makeOrder(Court.H02, "19:00", "20:00");
            List<Order> inputs = new ArrayList<>(List.of(hard1from18hTill19h, hard1from19hTill20h, hard2from18hTill19h, hard2from19hTill20h));

            Order expectedHard1 = makeOrder(Court.H01, "18:00", "20:00");
            Order expectedHard2 = makeOrder(Court.H02, "18:00", "20:00");
            List<Order> expected = List.of(expectedHard1, expectedHard2);

            List<Order> actual = SebOrderConverter.coalesce(inputs);
            assertEquals(expected, actual);
        }

        @Test
        void inSequenceButForDifferentCourts_coalescesForSameCourt() {
            Order hard1from18hTill19h = makeOrder(Court.H01, "18:00", "19:00");
            Order hard1from19hTill20h = makeOrder(Court.H01, "19:00", "20:00");
            Order hard2from18hTill19h = makeOrder(Court.H02, "20:00", "21:00");
            Order hard2from19hTill20h = makeOrder(Court.H02, "21:00", "22:00");
            List<Order> inputs = new ArrayList<>(List.of(hard1from18hTill19h, hard1from19hTill20h, hard2from18hTill19h, hard2from19hTill20h));

            Order expectedHard1 = makeOrder(Court.H01, "18:00", "20:00");
            Order expectedHard2 = makeOrder(Court.H02, "20:00", "22:00");
            List<Order> expected = List.of(expectedHard1, expectedHard2);

            List<Order> actual = SebOrderConverter.coalesce(inputs);
            assertEquals(expected, actual);
        }

    }

    @Nested
    class BoundaryConditions {

        @ParameterizedTest
        @EmptySource
        void whenInputIsEmpty_returnsEmpty(List<Order> inputs) {
            List<Order> expected = new ArrayList<>();
            List<Order> actual = SebOrderConverter.coalesce(inputs);
            assertEquals(actual, expected);
        }

        @ParameterizedTest
        @NullSource
        void whenInputIsNull_returnsNull(List<Order> inputs) {
            List<Order> expected = null;
            List<Order> actual = SebOrderConverter.coalesce(inputs);
            assertEquals(actual, expected);
        }

        @Test
        void whenOneOrder_returnsSameOrder() {
            Order order = makeOrder(Court.H01, "21:00", "22:00");
            List<Order> inputs = new ArrayList<>(List.of(order));

            List<Order> expected = List.of(order.clone());
            List<Order> actual = SebOrderConverter.coalesce(inputs);
            assertEquals(expected, actual);
        }

    }


    private static Order makeOrder(String date, Court court, String from, String to) {
        return new Order()
                .setDate(LocalDate.parse(date))
                .setCourt(court)
                .setTimeFrom(LocalTime.parse(from))
                .setTimeTo(LocalTime.parse(to));
    }

    private static Order makeOrder(String date, String from, String to) {
        return makeOrder(date, Court.H01, from, to);
    }

    private static Order makeOrder(Court court, String from, String to) {
        return makeOrder("2023-09-16", court, from, to);
    }

    private static Order makeOrder(String from, String to) {
        return makeOrder("2023-09-16", from, to);
    }
}