package com.butkus.tenniscrawler;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Example table (for visualization purposes):

 *                |  18:00 |  18:30 |  19:00 |  19:30 |  20:00 |
 * |--------------|--------|--------|--------|--------|--------|
 * | hard close   |   13   |    9   |    3   |    1   |    9   |
 * | hard far     |   14   |   10   |    4   |    2   |   10   |
 * | carpet close |   15   |   11   |    7   |    5   |   11   |
 * | carpet far   |   16   |   12   |    8   |    6   |   12   |
 */
class IndoorDetailedRecipeTest extends AbstractRecipeTest  {

    @Override
    Recipe getRecipe() {
        return new IndoorDetailedRecipe();
    }

    @Override
    List<CourtTypeAtHour> expectedFirst() {
        return List.of(new CourtTypeAtHour(CourtTypeCustom.HARD_CLOSE, LocalTime.parse("19:30")));
    }

    @Override
    List<CourtTypeAtHour> expectedSecond() {
        return List.of(new CourtTypeAtHour(CourtTypeCustom.HARD_FAR, LocalTime.parse("19:30")));
    }

    @Override
    List<CourtTypeAtHour> expectedLast() {
        return List.of(new CourtTypeAtHour(CourtTypeCustom.CARPET_FAR, LocalTime.parse("18:00")));
    }

    @Override
    List<Integer> getWeightsInOrder() {
        List<Integer> weights = new ArrayList<>();
        IntStream.rangeClosed(1, 16).forEach(weights::add);
        return weights;
    }

    @Override
    Integer getCourtCategoryCount() {
        return 4;
    }

    @Override
    List<Long> getAllRelevantCourtIds() {
        return Court.getIndoorIds();
    }
}