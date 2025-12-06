package com.butkus.tenniscrawler;

import java.time.LocalTime;
import java.util.List;

import static com.butkus.tenniscrawler.CourtTypeCustom.CLAY_SUMMER;
import static com.butkus.tenniscrawler.CourtTypeCustom.GRASS;

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
    List<CourtTypeAtHour> expectedFirst() {
        return List.of(new CourtTypeAtHour(CLAY_SUMMER, LocalTime.parse("19:00")));
    }

    @Override
    List<CourtTypeAtHour> expectedSecond() {
        return List.of(new CourtTypeAtHour(CLAY_SUMMER, LocalTime.parse("19:30")));
    }

    @Override
    List<CourtTypeAtHour> expectedLast() {
        return List.of(
                new CourtTypeAtHour(GRASS, LocalTime.parse("18:00")),
                new CourtTypeAtHour(GRASS, LocalTime.parse("20:00")));
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