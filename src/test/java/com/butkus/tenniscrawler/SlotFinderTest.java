package com.butkus.tenniscrawler;

import org.javatuples.Triplet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import static com.butkus.tenniscrawler.ExtensionInterest.EARLIER;
import static com.butkus.tenniscrawler.ExtensionInterest.LATER;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

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
                assertOfferFound(List.of(0,0,0,1,4,4), EARLIER);
            }

            @Test
            void findsNonAdjacentEarlierTime() {
                assertOfferFound(List.of(0,1,1,0,4,4), EARLIER);
            }
        }

        @Nested
        class Later {

            @Test
            void findsAdjacentLaterTime() {
                assertOfferFound(List.of(0,4,4,1,0,0), LATER);
            }

            @Test
            void findsNonAdjacentLaterTime() {
                assertOfferFound(List.of(0,4,4,0,1,1), LATER);
            }
        }
    }

    @Nested
    class OtherCourt {

        Court currentCourt = Court.HARD;
        Court otherCourt1 = null;
        Court otherCourt2 = null;

        @BeforeEach
        void setUp() {
            for (Court court : Court.values()) {
                if (court != currentCourt) {
                    otherCourt1 = court;
                    break;
                }
            }
            for (Court court : Court.values()) {
                if (court != currentCourt && court != otherCourt1) {
                    otherCourt2 = court;
                    break;
                }
            }
            if (otherCourt1 == null || otherCourt2 == null) fail("cannot find 2 other courts");
        }
        @Nested
        class Earlier {

            @Nested
            class OverlappingEarlierTime {

                @ParameterizedTest
                @EnumSource(value = Court.class, names = "HARD", mode = EnumSource.Mode.EXCLUDE)
                void findsForAllOtherCourts(Court otherCourt) {
                    cache.addIfCacheable(keyFrom(date, otherCourt.getCourtId()), List.of(0,0,0,0,4,4));
                    assertOfferFound(List.of(0,0,0,1,1,0), EARLIER);
                }

                @Test
                void other_2_courtsHaveReservations_1stOneNoExtension_2ndOneYesExtension_findsTime() {
                    cache.addIfCacheable(keyFrom(date, otherCourt1.getCourtId()), List.of(4,4,0,0,0,0)); // is earliest, cannot find earlier for this
                    cache.addIfCacheable(keyFrom(date, otherCourt2.getCourtId()), List.of(0,0,0,0,4,4)); // is latest, any earlier will fit EARLIER ExtensionInterest

                    assertOfferFound(List.of(0,0,0,1,1,0), EARLIER);
                }

            }

            @Nested
            class AdjacentEarlierTime {

                @ParameterizedTest
                @EnumSource(value = Court.class, names = "HARD", mode = EnumSource.Mode.EXCLUDE)
                void findsForAllOtherCourts(Court otherCourt) {
                    cache.addIfCacheable(keyFrom(date, otherCourt.getCourtId()), List.of(0, 0, 0, 0, 4, 4));
                    assertOfferFound(List.of(0,0,1,1,0,0), EARLIER);
                }

                @Test
                void other_2_courtsHaveReservations_1stOneNoExtension_2ndOneYesExtension_findsTime() {
                    cache.addIfCacheable(keyFrom(date, otherCourt1.getCourtId()), List.of(4,4,0,0,0,0)); // is earliest, cannot find earlier for this
                    cache.addIfCacheable(keyFrom(date, otherCourt2.getCourtId()), List.of(0,0,0,0,4,4)); // is latest, any earlier will fit EARLIER ExtensionInterest

                    assertOfferFound(List.of(0,0,1,1,0,0), EARLIER);
                }

            }

            @Nested
            class NonAdjacentEarlierTime {

                @ParameterizedTest
                @EnumSource(value = Court.class, names = "HARD", mode = EnumSource.Mode.EXCLUDE)
                void findsForAllOtherCourts(Court otherCourt) {
                    cache.addIfCacheable(keyFrom(date, otherCourt.getCourtId()), List.of(0,0,0,0,4,4));
                    assertOfferFound(List.of(0,1,1,0,0,0), EARLIER);
                }

                @Test
                void other_2_courtsHaveReservations_1stOneNoExtension_2ndOneYesExtension_findsTime() {
                    cache.addIfCacheable(keyFrom(date, otherCourt1.getCourtId()), List.of(4,4,0,0,0,0)); // is earliest, cannot find earlier for this
                    cache.addIfCacheable(keyFrom(date, otherCourt2.getCourtId()), List.of(0,0,0,0,4,4)); // is latest, any earlier will fit EARLIER ExtensionInterest

                    assertOfferFound(List.of(0,1,1,0,0,0), EARLIER);
                }

            }

        }
        @Nested
        class Later {

            @Nested
            class OverlappingLaterTime {

                @ParameterizedTest
                @EnumSource(value = Court.class, names = "HARD", mode = EnumSource.Mode.EXCLUDE)
                void findsForAllOtherCourts(Court otherCourt) {
                    cache.addIfCacheable(keyFrom(date, otherCourt.getCourtId()), List.of(4,4,0,0,0,0));
                    assertOfferFound(List.of(0,1,1,0,0,0), LATER);
                }

                @Test
                void other_2_courtsHaveReservations_1stOneNoExtension_2ndOneYesExtension_findsTime() {
                    cache.addIfCacheable(keyFrom(date, otherCourt1.getCourtId()), List.of(0,0,0,0,4,4)); // is latest, cannot find later for this
                    cache.addIfCacheable(keyFrom(date, otherCourt2.getCourtId()), List.of(4,4,0,0,0,0)); // is earliest, any later will fit LATER ExtensionInterest

                    assertOfferFound(List.of(0,1,1,0,0,0), LATER);
                }

            }

            @Nested
            class AdjacentLaterTime {

                @ParameterizedTest
                @EnumSource(value = Court.class, names = "HARD", mode = EnumSource.Mode.EXCLUDE)
                void findsForAllOtherCourts(Court otherCourt) {
                    cache.addIfCacheable(keyFrom(date, otherCourt.getCourtId()), List.of(4,4,0,0,0,0));
                    assertOfferFound(List.of(0,0,1,1,0,0), LATER);
                }

                @Test
                void other_2_courtsHaveReservations_1stOneNoExtension_2ndOneYesExtension_findsTime() {
                    cache.addIfCacheable(keyFrom(date, otherCourt1.getCourtId()), List.of(0,0,0,0,4,4)); // is latest, cannot find later for this
                    cache.addIfCacheable(keyFrom(date, otherCourt2.getCourtId()), List.of(4,4,0,0,0,0)); // is earliest, any later will fit LATER ExtensionInterest

                    assertOfferFound(List.of(0,0,1,1,0,0), LATER);
                }

            }

            @Nested
            class NonAdjacentLaterTime {

                @ParameterizedTest
                @EnumSource(value = Court.class, names = "HARD", mode = EnumSource.Mode.EXCLUDE)
                void findsNonAdjacentLaterTime(Court otherCourt) {
                    cache.addIfCacheable(keyFrom(date, otherCourt.getCourtId()), List.of(4,4,0,0,0,0));
                    assertOfferFound(List.of(0,0,0,1,1,0), LATER);
                }

                @Test
                void other_2_courtsHaveReservations_1stOneNoExtension_2ndOneYesExtension_findsTime() {
                    cache.addIfCacheable(keyFrom(date, otherCourt1.getCourtId()), List.of(0,0,0,0,4,4)); // is latest, cannot find later for this
                    cache.addIfCacheable(keyFrom(date, otherCourt2.getCourtId()), List.of(4,4,0,0,0,0)); // is earliest, any later will fit LATER ExtensionInterest

                    assertOfferFound(List.of(0,0,0,1,1,0), LATER);
                }

            }

        }

    }

    private Triplet<LocalDate, Integer, ExtensionInterest> keyFrom(LocalDate date, int courtId) {
        ExtensionInterest anyExtension = EARLIER;
        return Triplet.with(date, courtId, anyExtension);
    }

    private void assertOfferFound(List<Integer> currentCourtAggregated, ExtensionInterest extensionInterest) {
        slotFinder = new SlotFinder(cache, currentCourtAggregated, date, Court.HARD, extensionInterest);
        assertTrue(slotFinder.isOfferFound());
    }
}