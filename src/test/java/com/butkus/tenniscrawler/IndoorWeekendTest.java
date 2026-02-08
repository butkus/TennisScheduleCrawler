package com.butkus.tenniscrawler;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Example table (for visualization purposes):

 *                |  17:00 |  17:30 |  18:00 |  18:30 |  19:00 |  19:30 |  20:00 |
 * |--------------|--------|--------|--------|--------|--------|--------|--------|
 * | hard close   |   21   |   13   |    1   |    5   |    9   |   17   |   21   |
 * | hard far     |   22   |   14   |    2   |    6   |   10   |   18   |   22   |
 * | carpet close |   23   |   15   |    3   |    7   |   11   |   19   |   23   |
 * | carpet far   |   24   |   16   |    4   |    8   |   12   |   20   |   24   |
 */
class IndoorWeekendTest extends AbstractRecipeTest  {

    @Override
    Recipe getRecipe() {
        return new IndoorWeekend();
    }

    @Override
    List<CourtTypeAtHour> expectedFirst() {
        return List.of(new CourtTypeAtHour(CourtTypeCustom.HARD_CLOSE, LocalTime.parse("18:00")));
    }

    @Override
    List<CourtTypeAtHour> expectedSecond() {
        return List.of(new CourtTypeAtHour(CourtTypeCustom.HARD_FAR, LocalTime.parse("18:00")));
    }

    @Override
    List<CourtTypeAtHour> expectedLast() {
        return List.of(
                new CourtTypeAtHour(CourtTypeCustom.CARPET_FAR, LocalTime.parse("17:00")),
                new CourtTypeAtHour(CourtTypeCustom.CARPET_FAR, LocalTime.parse("20:00")));
    }

    @Override
    List<Integer> getWeightsInOrder() {
        List<Integer> weights = new ArrayList<>();
        IntStream.rangeClosed(1, 24).forEach(weights::add);
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