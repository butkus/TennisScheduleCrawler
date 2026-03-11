package com.butkus.tenniscrawler;

import java.time.LocalTime;
import java.util.List;

import static com.butkus.tenniscrawler.CourtGroup.HARD;

/**
 * Example table (for visualization purposes):

 *                |  18:00 |  18:30 |  19:00 |  19:30 |  20:00 |
 * |--------------|--------|--------|--------|--------|--------|
 * | hard         |    4   |    3   |    2   |    1   |    3   |
 * | carpet       |    5   |    4   |    3   |    2   |    4   |
 */
class IndoorSimpleTest extends AbstractRecipeTest {

    @Override
    Recipe getRecipe() {
        return new IndoorSimple();
    }

    @Override
    List<CourtGroupAtHour> expectedFirst() {
        return List.of(new CourtGroupAtHour(HARD, LocalTime.parse("19:30")));
    }

    @Override
    List<CourtGroupAtHour> expectedSecond() {
        return List.of(
                new CourtGroupAtHour(CourtGroup.HARD, LocalTime.parse("19:00")),
                new CourtGroupAtHour(CourtGroup.CARPET, LocalTime.parse("19:30"))
        );
    }

    @Override
    List<CourtGroupAtHour> expectedLast() {
        return List.of(
                new CourtGroupAtHour(CourtGroup.CARPET, LocalTime.parse("18:00")));
    }

    @Override
    List<Integer> getWeightsInOrder() {
        return List.of(1, 2, 3, 4, 5);
    }

    @Override
    Integer getCourtCategoryCount() {
        return 2;
    }

    @Override
    List<Long> getAllRelevantCourtIds() {
        return Court.getIndoorIds();
    }
}