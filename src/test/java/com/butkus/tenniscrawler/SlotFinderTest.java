package com.butkus.tenniscrawler;

import org.javatuples.Triplet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import static com.butkus.tenniscrawler.Court.CARPET;
import static com.butkus.tenniscrawler.ExtensionInterest.EARLIER;
import static com.butkus.tenniscrawler.ExtensionInterest.LATER;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SlotFinderTest {

    SlotFinder slotFinder;
    private Cache cache;
    private LocalDate date;

    @BeforeEach
    void setUp() {
        date = LocalDate.now();
        cache = new Cache(Duration.ofMinutes(5));

    }

    @Nested
    class CurrentCourt {

        @Nested
        class Earlier {

            @Test
            void findsAdjacentEarlierTime() {
                List<Integer> aggregatedCourt = List.of(0,0,0,1,4,4);
                slotFinder = new SlotFinder(cache, aggregatedCourt, date, Court.HARD, "Kieta danga", EARLIER);

                assertTrue(slotFinder.isOfferFound());
            }

            @Test
            void findsNonAdjacentEarlierTime() {
                List<Integer> aggregatedCourt = List.of(0,1,1,0,4,4);
                slotFinder = new SlotFinder(cache, aggregatedCourt, date, Court.HARD, "Kieta danga", EARLIER);

                assertTrue(slotFinder.isOfferFound());
            }
        }

        @Nested
        class Later {

            @Test
            void findsAdjacentLaterTime() {
                List<Integer> aggregatedCourt = List.of(0,4,4,1,0,0);
                slotFinder = new SlotFinder(cache, aggregatedCourt, date, Court.HARD, "Kieta danga", LATER);

                assertTrue(slotFinder.isOfferFound());
            }

            @Test
            void findsNonAdjacentLaterTime() {
                List<Integer> aggregatedCourt = List.of(0,4,4,0,1,1);
                slotFinder = new SlotFinder(cache, aggregatedCourt, date, Court.HARD, "Kieta danga", LATER);

                assertTrue(slotFinder.isOfferFound());
            }
        }
    }

    @Nested
    class OtherCourt {

        @Nested
        class Earlier {

            @Test
            void otherCourt_findsOverlappingEarlierTime() {
                cache.addIfCacheable(keyFrm(date, CARPET), List.of(0,0,0,0,4,4));

                List<Integer> aggregatedCourt = List.of(0,0,0,1,1,0);
                slotFinder = new SlotFinder(cache, aggregatedCourt, date, Court.HARD, "Kieta danga", EARLIER);

                assertTrue(slotFinder.isOfferFound());
            }

            @Test
            void otherCourt_findsAdjacentEarlierTime() {
                cache.addIfCacheable(keyFrm(date, CARPET), List.of(0,0,0,0,4,4));

                List<Integer> aggregatedCourt = List.of(0,0,1,1,0,0);
                slotFinder = new SlotFinder(cache, aggregatedCourt, date, Court.HARD, "Kieta danga", EARLIER);

                assertTrue(slotFinder.isOfferFound());
            }

            @Test
            void otherCourt_findsNonAdjacentEarlierTime() {
                cache.addIfCacheable(keyFrm(date, CARPET), List.of(0,0,0,0,4,4));

                List<Integer> aggregatedCourt = List.of(0,1,1,0,0,0);
                slotFinder = new SlotFinder(cache, aggregatedCourt, date, Court.HARD, "Kieta danga", EARLIER);

                assertTrue(slotFinder.isOfferFound());
            }

        }

        @Nested
        class Later {

            @Test
            void findsOverlappingLaterTime() {
                cache.addIfCacheable(keyFrm(date, CARPET), List.of(4,4,0,0,0,0));

                List<Integer> aggregatedCourt = List.of(0,1,1,0,0,0);
                slotFinder = new SlotFinder(cache, aggregatedCourt, date, Court.HARD, "Kieta danga", LATER);

                assertTrue(slotFinder.isOfferFound());
            }

            @Test
            void findsAdjacentLaterTime() {
                cache.addIfCacheable(keyFrm(date, CARPET), List.of(4,4,0,0,0,0));

                List<Integer> aggregatedCourt = List.of(0,0,1,1,0,0);
                slotFinder = new SlotFinder(cache, aggregatedCourt, date, Court.HARD, "Kieta danga", LATER);

                assertTrue(slotFinder.isOfferFound());
            }

            @Test
            void findsNonAdjacentLaterTime() {
                cache.addIfCacheable(keyFrm(date, CARPET), List.of(4,4,0,0,0,0));

                List<Integer> aggregatedCourt = List.of(0,0,0,1,1,0);
                slotFinder = new SlotFinder(cache, aggregatedCourt, date, Court.HARD, "Kieta danga", LATER);

                assertTrue(slotFinder.isOfferFound());
            }

        }

    }

    private Triplet<LocalDate, Integer, ExtensionInterest> keyFrm(LocalDate date, int courtId) {
        ExtensionInterest anyExtension = EARLIER;
        return Triplet.with(date, courtId, anyExtension);
    }
}