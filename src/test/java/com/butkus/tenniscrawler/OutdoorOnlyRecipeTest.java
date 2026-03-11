package com.butkus.tenniscrawler;

import java.time.LocalTime;
import java.util.List;

import static com.butkus.tenniscrawler.CourtGroup.CLAY_SUMMER;
import static com.butkus.tenniscrawler.CourtGroup.GRASS;

/**
 * Example table (for visualization purposes):

 *                |  18:00 |  18:30 |  19:00 |  19:30 |  20:00 |
 * |--------------|--------|--------|--------|--------|--------|
 * | grass        |   10   |    9   |    7   |    8   |   10   |
 * | clay summer  |    5   |    4   |    2   |    3   |    5   |
 * | clay rest    |    7   |    6   |    4   |    5   |    7   |
 */
class OutdoorOnlyRecipeTest extends AbstractRecipeTest {

    @Override
    Recipe getRecipe() {
        return new OutdoorOnlyRecipe();
    }

    @Override
    List<CourtGroupAtHour> expectedFirst() {
        return List.of(new CourtGroupAtHour(CLAY_SUMMER, LocalTime.parse("19:00")));
    }

    @Override
    List<CourtGroupAtHour> expectedSecond() {
        return List.of(new CourtGroupAtHour(CLAY_SUMMER, LocalTime.parse("19:30")));
    }

    @Override
    List<CourtGroupAtHour> expectedLast() {
        return List.of(
                new CourtGroupAtHour(GRASS, LocalTime.parse("18:00")),
                new CourtGroupAtHour(GRASS, LocalTime.parse("20:00")));
    }

    @Override
    List<Integer> getWeightsInOrder() {
        return List.of(2, 3, 4, 5, 6, 7, 8, 9, 10);
    }

    @Override
    Integer getCourtCategoryCount() {
        return 3;
    }

    @Override
    List<Long> getAllRelevantCourtIds() {
        return Court.getOutdoorIds();
    }

}