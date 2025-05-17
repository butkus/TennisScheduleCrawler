package com.butkus.tenniscrawler;

import com.butkus.tenniscrawler.rest.orders.Order;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.butkus.tenniscrawler.ExtensionInterest.ANY;
import static org.junit.jupiter.api.Assertions.*;

/**
 * <h>General idea</h>
 * <li>NOT ALL Desires have orders (maybe I have a 99 periodic Desires way into the future)</li>
 * <li>ALL Orders MUST HAVE Desires (If I don't want any 'better' orders for day/court, make a Desire with NONE)</li>
 */
class DesireOrderPairerTest {

    public static final LocalDate DAY = LocalDate.parse("2023-10-01");
    public static final LocalDate NEXT_DAY = LocalDate.parse("2023-10-02");
    public static final LocalTime TIME_1800 = LocalTime.parse("18:00");
    public static final LocalTime TIME_1900 = LocalTime.parse("19:00");

    @Test
    void orderMatchesDesire_byDayAndCourt_ok() {
        Order order = new Order(DAY, Court.H01, TIME_1800, TIME_1900);
        Desire desire = new Desire(DAY, ANY, Court.getIndoorIds());
        DesireOrderPairer pairer = new DesireOrderPairer(listOf(desire), listOf(order));
        pairer.pair();
        assertEquals(order, desire.getOrder());
    }

    @Test
    void orderDoesNotHaveDesire_byDay_throws() {
        Order order = new Order(DAY, Court.H01, TIME_1800, TIME_1900);
        Desire desire = new Desire(NEXT_DAY, ANY, Court.getIndoorIds());
        DesireOrderPairer pairer = new DesireOrderPairer(listOf(desire), listOf(order));
        assertThrows(OrderWithoutDesireException.class, pairer::pair);
    }

    @Test
    void orderDoesNotHaveDesire_byCourt_throws() {
        Order order = new Order(DAY, Court.H01, TIME_1800, TIME_1900);
        Desire desire = new Desire(DAY, ANY, Court.getOutdoorIds());
        DesireOrderPairer pairer = new DesireOrderPairer(listOf(desire), listOf(order));
        assertThrows(OrderWithoutDesireException.class, pairer::pair);
    }

    @Nested
    class Cat1_moreDesiresThanOrders {

        // todo refactor those 2 tests into 1?
        @Test
        void desireIn_and_desireOut_orderIn_pairsToIn() {
            Desire desireIn = new Desire(DAY, ANY, Court.getIndoorIds());
            Desire desireOut = new Desire(DAY, ANY, Court.getOutdoorIds());
            Order orderIn = new Order(DAY, Court.H01, TIME_1800, TIME_1900);
            DesireOrderPairer pairer = new DesireOrderPairer(listOf(desireIn, desireOut), listOf(orderIn));
            pairer.pair();
            assertEquals(desireIn.getOrder(), orderIn);
            assertNull(desireOut.getOrder());
        }

        @Test
        void desireIn_and_desireOut_orderOut_pairsToOut() {
            Desire desireIn = new Desire(DAY, ANY, Court.getIndoorIds());
            Desire desireOut = new Desire(DAY, ANY, Court.getOutdoorIds());
            Order orderOut = new Order(DAY, Court.G1, TIME_1800, TIME_1900);
            DesireOrderPairer pairer = new DesireOrderPairer(listOf(desireIn, desireOut), listOf(orderOut));
            pairer.pair();
            assertNull(desireIn.getOrder());
            assertEquals(desireOut.getOrder(), orderOut);
        }
    }

    @Nested
    class Cat2_moreOrdersThanDesires {

        @Test
        void twoOrders_oneDesire_throws() {
            Order orderHard = new Order(DAY, Court.H01, TIME_1800, TIME_1900);
            Order orderGrass = new Order(DAY, Court.G1, TIME_1800, TIME_1900);
            Desire desire = new Desire(DAY, ANY, Court.getIds());
            DesireOrderPairer pairer = new DesireOrderPairer(listOf(desire), listOf(orderHard, orderGrass));
            assertThrows(OrderWithoutDesireException.class, pairer::pair);
        }
    }

    @Nested
    class Cat3_sameAmountOrdersAndDesires {

        @Test
        void orderIn_orderOut_and_desireIn_desireOut_shouldMatchRegardlessOfOrder() {
            Order orderIn = new Order(DAY, Court.H01, TIME_1800, TIME_1900);
            Order orderOut = new Order(DAY, Court.G2, TIME_1800, TIME_1900);
            Desire desireIn = new Desire(DAY, ANY, Court.getIndoorIds());
            Desire desireOut = new Desire(DAY, ANY, Court.getOutdoorIds());
            DesireOrderPairer pairerInOrder = new DesireOrderPairer(listOf(desireIn, desireOut), listOf(orderIn, orderOut));
            pairerInOrder.pair();
            assertEquals(desireIn.getOrder(), orderIn);
            assertEquals(desireOut.getOrder(), orderOut);

            reset(desireIn, desireOut);

            DesireOrderPairer pairerOutOfOrder = new DesireOrderPairer(listOf(desireOut, desireIn), listOf(orderIn, orderOut));
            pairerOutOfOrder.pair();
            assertEquals(desireIn.getOrder(), orderIn);
            assertEquals(desireOut.getOrder(), orderOut);
        }

        @Test
        void orderIn_orderOut_and_desireIn_desireAny_shouldMatchRegardlessOfOrder() {
            Order orderIn = new Order(DAY, Court.H01, TIME_1800, TIME_1900);
            Order orderOut = new Order(DAY, Court.G2, TIME_1800, TIME_1900);
            Desire desireIn = new Desire(DAY, ANY, Court.getIndoorIds());
            Desire desireAny = new Desire(DAY, ANY, Court.getNonSquashIds());       // note that it's not  Court.getIds()  as it would include squash courts

            DesireOrderPairer pairerInOrder = new DesireOrderPairer(listOf(desireIn, desireAny), listOf(orderIn, orderOut));
            assertDoesNotThrow(pairerInOrder::pair);
            assertEquals(desireIn.getOrder(), orderIn);
            assertEquals(desireAny.getOrder(), orderOut);

            reset(desireIn, desireAny);

            DesireOrderPairer pairerOutOfOrder = new DesireOrderPairer(listOf(desireAny, desireIn), listOf(orderIn, orderOut));
            assertDoesNotThrow(pairerOutOfOrder::pair);
            assertEquals(desireIn.getOrder(), orderIn);
            assertEquals(desireAny.getOrder(), orderOut);
        }

        private void reset(Desire... desires) {
            Arrays.stream(desires).forEach(desire -> desire.setOrder(null));
        }

        @Test
        void orderHard_orderCarpet_desireIn_desireIn_shouldFailBecauseAmbiguous() {
            Order orderHard = new Order(DAY, Court.H01, TIME_1800, TIME_1900);
            Order orderCarpet = new Order(DAY, Court.K1, TIME_1800, TIME_1900);
            Desire desireIn = new Desire(DAY, ANY, Court.getIndoorIds());
            Desire desireInAlso = new Desire(DAY, ANY, Court.getIndoorIds());
            DesireOrderPairer pairer = new DesireOrderPairer(listOf(desireIn, desireInAlso), listOf(orderHard, orderCarpet));
            assertThrows(DuplicateDesiresException.class, pairer::pair);
        }
    }

    private static <T> List<T> listOf(T... items) {
        ArrayList<T> result = new ArrayList<>();
        Collections.addAll(result, items);
        return result;
    }
}