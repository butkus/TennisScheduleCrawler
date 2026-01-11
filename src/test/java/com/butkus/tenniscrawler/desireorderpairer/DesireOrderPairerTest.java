package com.butkus.tenniscrawler.desireorderpairer;

import com.butkus.tenniscrawler.*;
import com.butkus.tenniscrawler.rest.orders.Order;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.butkus.tenniscrawler.ExtensionInterest.ANY;
import static org.junit.jupiter.api.Assertions.*;

/**
 * <h2>General idea</h2>
 * <li>NOT ALL Desires have orders (maybe I have 99 periodic Desires way into the future)</li>
 * <li>ALL Orders MUST HAVE Desires (If I don't want any 'better' orders for day/court, make a Desire with NONE)</li>
 * <br/>
 *
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
class DesireOrderPairerTest {

    public static final LocalDate DAY = LocalDate.parse("2023-10-01");
    public static final LocalDate NEXT_DAY = LocalDate.parse("2023-10-02");
    public static final LocalTime TIME_1800 = LocalTime.parse("18:00");
    public static final LocalTime TIME_1900 = LocalTime.parse("19:00");

    public static final Recipe INDOOR_RECIPE = new IndoorSimpleRecipe();
    public static final Recipe OUTDOOR_RECIPE = new OutdoorOnlyRecipe();

    @ParameterizedTest
    @MethodSource("orderMatchesDesire_byDayAndCourt_args")
    void orderMatchesDesire_byDayAndCourt_ok(Desire desire) {
        Order order = new Order(DAY, Court.H01, TIME_1800, TIME_1900);
        DesireOrderPairer pairer = new DesireOrderPairer(listOf(desire), listOf(order));
        pairer.pair();
        assertEquals(order, desire.getOrder());
    }
    static List<Desire> orderMatchesDesire_byDayAndCourt_args() {
        return List.of(
                new Desire(DAY, ANY, Court.getIndoorIds()),
                new Desire(DAY, INDOOR_RECIPE)
        );
    }


    @ParameterizedTest
    @MethodSource("orderDoesNotHaveDesire_byDay_args")
    void orderDoesNotHaveDesire_byDay_throws(Desire desire) {
        Order order = new Order(DAY, Court.H01, TIME_1800, TIME_1900);
        DesireOrderPairer pairer = new DesireOrderPairer(listOf(desire), listOf(order));
        assertThrows(OrderWithoutDesireException.class, pairer::pair);
    }
    static List<Desire> orderDoesNotHaveDesire_byDay_args() {
        return Arrays.asList(
                new Desire(NEXT_DAY, ANY, Court.getIndoorIds()),
                new Desire(NEXT_DAY, INDOOR_RECIPE)
        );
    }


    @ParameterizedTest
    @MethodSource("orderDoesNotHaveDesire_byCourt_args")
    void orderDoesNotHaveDesire_byCourt_throws(Desire desire) {
        Order order = new Order(DAY, Court.H01, TIME_1800, TIME_1900);
        DesireOrderPairer pairer = new DesireOrderPairer(listOf(desire), listOf(order));
        assertThrows(OrderWithoutDesireException.class, pairer::pair);
    }
    static List<Desire> orderDoesNotHaveDesire_byCourt_args() {
        return Arrays.asList(
                new Desire(DAY, ANY, Court.getOutdoorIds()),
                new Desire(DAY, OUTDOOR_RECIPE)
        );
    }


    @Nested
    class Cat1_moreDesiresThanOrders {

        // NOTE: these desires cloned for each test iteration by new DesireOrderPairTestCase()...build() method
        // If we want to move them to the top of root class, we need to ensure cloning or resetting before/after each test
        public static final Desire DESIRE_IN = new Desire(DAY, ANY, Court.getIndoorIds());
        public static final Desire DESIRE_OUT = new Desire(DAY, ANY, Court.getOutdoorIds());
        public static final Desire DESIRE_RECIPE_IN = new Desire(DAY, INDOOR_RECIPE);
        public static final Desire DESIRE_RECIPE_OUT = new Desire(DAY, OUTDOOR_RECIPE);
        public static final Order ORDER_OUT = new Order(DAY, Court.G1, TIME_1800, TIME_1900);
        public static final Order ORDER_IN = new Order(DAY, Court.H01, TIME_1800, TIME_1900);

        @ParameterizedTest
        @MethodSource("desireOrderPairTestCases")
        void _2desires_1order_pairsOne(DesireOrderPairTestCase testCase) {
            DesireOrderPairer pairer = new DesireOrderPairer(listOf(testCase.desireIn, testCase.desireOut), listOf(testCase.order));
            pairer.pair();
            assertEquals(testCase.expected.getOrder(), testCase.order);
            assertNull(testCase.unexpected.getOrder());
        }

        static List<DesireOrderPairTestCase> desireOrderPairTestCases() {
            return List.of(
                    new DesireOrderPairTestCase().desireIn(DESIRE_IN).desireOut(DESIRE_OUT)
                            .order(ORDER_IN)
                            .expected(DESIRE_IN).unexpected(DESIRE_OUT).build(),
                    new DesireOrderPairTestCase().desireIn(DESIRE_RECIPE_IN).desireOut(DESIRE_RECIPE_OUT)
                            .order(ORDER_IN)
                            .expected(DESIRE_RECIPE_IN).unexpected(DESIRE_RECIPE_OUT).build(),
                    new DesireOrderPairTestCase()
                            .desireIn(DESIRE_IN).desireOut(DESIRE_OUT)
                            .order(ORDER_OUT)
                            .expected(DESIRE_OUT).unexpected(DESIRE_IN).build(),
                    new DesireOrderPairTestCase()
                            .desireIn(DESIRE_RECIPE_IN).desireOut(DESIRE_RECIPE_OUT)
                            .order(ORDER_OUT)
                            .expected(DESIRE_RECIPE_OUT).unexpected(DESIRE_RECIPE_IN).build()
            );
        }
    }


    @Nested
    class Cat2_moreOrdersThanDesires {

        // NOTE: the following test does not have Desire-with-Recipe variant.
        //  this test is kind of superfluous in any case, but with Recipes it just does not work.
        //  it works with old Desire format because Desire's court list is just a list of court id's
        //  with Recipes, I differentiated outside vs inside.
        //  I don't expect to change that because if weather's good, I want outside. If weather's bad, I want inside.
        //  As a result, there's no InsideAndOutsideRecipe, thus I can't write similar test with Recipes.
        //  I could change this test to have 2 orders outside (or 2 orders inside) but that would throw
        //  DuplicateOrdersException instead of OrderWithoutDesireException, thus changing the nature of the test.
        //  Again, this is a rather contrived test, made in part to show that Cat2_moreOrdersThanDesires is considered.
        //  Lastly, OrderWithoutDesireException is covered in 2 tests above already:
        //    - orderDoesNotHaveDesire_byDay_throws()
        //    - orderDoesNotHaveDesire_byCourt_throws()

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

        @ParameterizedTest
        @MethodSource("_2orders_2desires_pairing_args")
        void orderIn_orderOut_and_desireIn_desireOut_shouldMatchRegardlessOfOrder(Desire desireIn, Desire desireOut) {
            Order orderIn = new Order(DAY, Court.H01, TIME_1800, TIME_1900);
            Order orderOut = new Order(DAY, Court.G2, TIME_1800, TIME_1900);

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
        private static List<Arguments> _2orders_2desires_pairing_args() {
            Desire desireIn = new Desire(DAY, ANY, Court.getIndoorIds());
            Desire desireOut = new Desire(DAY, ANY, Court.getOutdoorIds());
            Desire desireRecipeIn = new Desire(DAY, INDOOR_RECIPE);
            Desire desireRecipeOut = new Desire(DAY, OUTDOOR_RECIPE);
            return List.of(
                    Arguments.of(desireIn, desireOut),
                    Arguments.of(desireRecipeIn, desireRecipeOut)
            );
        }


        // NOTE: the following test does not have Desire-with-Recipe variant because RecipeAny does not exist.
        //   see comment above twoOrders_oneDesire_throws() test.
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

        @Nested
        class Ambiguous {

            // NOTE: the following test does not have Desire-with-Recipe variant because RecipeAny does not exist.
            //   see comment above twoOrders_oneDesire_throws() test.
            // ambiguous ORDERS: order 1 can be mapped to any desire; so does order 2. therefore pairing cannot be made canonical
            @Test
            void orderIn_orderIn_and_desireIn_desireAny_shouldThrowBecauseAmbiguous() {
                Order orderIn1 = new Order(DAY, Court.H01, TIME_1800, TIME_1900);
                Order orderIn2 = new Order(DAY, Court.K1, TIME_1800, TIME_1900);
                Desire desireIn = new Desire(DAY, ANY, Court.getIndoorIds());
                Desire desireAny = new Desire(DAY, ANY, Court.getNonSquashIds());       // note that it's not  Court.getIds()  as it would include squash courts

                // below all permutations are tested:

                DesireOrderPairer pairer1 = new DesireOrderPairer(listOf(desireIn, desireAny), listOf(orderIn1, orderIn2));
                assertThrows(DuplicateOrdersException.class, pairer1::pair);
                reset(desireIn, desireAny);

                DesireOrderPairer pairer2 = new DesireOrderPairer(listOf(desireAny, desireIn), listOf(orderIn1, orderIn2));
                assertThrows(DuplicateOrdersException.class, pairer2::pair);
                reset(desireIn, desireAny);

                DesireOrderPairer pairer3 = new DesireOrderPairer(listOf(desireIn, desireAny), listOf(orderIn2, orderIn1));
                assertThrows(DuplicateOrdersException.class, pairer3::pair);
                reset(desireIn, desireAny);

                DesireOrderPairer pairer4 = new DesireOrderPairer(listOf(desireAny, desireIn), listOf(orderIn2, orderIn1));
                assertThrows(DuplicateOrdersException.class, pairer4::pair);
            }

            // not Cat3, but kinda belongs here. Plus, if/when we have more than indoor/outdoor categories, it will be easy to refactor it so it is Cat3 and test will perform the same
            // ambiguous DESIRES: even though order is just 1, it is not clear which desire should be paired with it
            @ParameterizedTest
            @MethodSource("orderHard_desireIn_desireIn_shouldFailBecauseAmbiguous_args")
            void orderHard_desireIn_desireIn_shouldFailBecauseAmbiguous(Desire desireIn1, Desire desireIn2) {
                Order orderHard = new Order(DAY, Court.H01, TIME_1800, TIME_1900);
                DesireOrderPairer pairer = new DesireOrderPairer(listOf(desireIn1, desireIn2), listOf(orderHard));
                assertThrows(DuplicateDesiresException.class, pairer::pair);
            }
            private static List<Arguments> orderHard_desireIn_desireIn_shouldFailBecauseAmbiguous_args() {
                Desire desireIn1 = new Desire(DAY, ANY, Court.getIndoorIds());
                Desire desireIn2 = new Desire(DAY, ANY, Court.getIndoorIds());
                Desire desireRecipeIn1 = new Desire(DAY, INDOOR_RECIPE);
                Desire desireRecipeIn2 = new Desire(DAY, INDOOR_RECIPE);
                return List.of(
                        Arguments.of(desireIn1, desireIn2),
                        Arguments.of(desireRecipeIn1, desireRecipeIn2)
                );
            }
        }

    }

    private static <T> List<T> listOf(T... items) {
        ArrayList<T> result = new ArrayList<>();
        Collections.addAll(result, items);
        return result;
    }

    private void reset(Desire... desires) {
        Arrays.stream(desires).forEach(desire -> desire.setOrder(null));
    }
}