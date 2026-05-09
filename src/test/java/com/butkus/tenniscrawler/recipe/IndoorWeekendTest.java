package com.butkus.tenniscrawler.recipe;

import com.butkus.tenniscrawler.Court;
import com.butkus.tenniscrawler.CourtGroup;
import com.butkus.tenniscrawler.CourtGroupAtHour;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Example table (for visualization purposes):

 *                |  17:00 |  17:30 |  18:00 |  18:30 |  19:00 |  19:30 |  20:00 |
 * |--------------|--------|--------|--------|--------|--------|--------|--------|
 * | hard good    |   26   |   13   |    1   |    5   |    9   |   20   |   26   |
 * | hard meh     |   27   |   14   |    2   |    6   |   10   |   21   |   27   |
 * | carpet close |   28   |   15   |    3   |    7   |   11   |   22   |   28   |
 * | carpet far   |   29   |   16   |    4   |    8   |   12   |   23   |   29   |
 * | hard bad     |   30   |   24   |   17   |   18   |   19   |   25   |   30   |
 */
class IndoorWeekendTest extends AbstractRecipeTest {

    @Override
    Recipe getRecipe() {
        return new IndoorWeekend();
    }

    @Override
    List<CourtGroupAtHour> expectedFirst() {
        return List.of(new CourtGroupAtHour(CourtGroup.HARD_GOOD, LocalTime.parse("18:00")));
    }

    @Override
    List<CourtGroupAtHour> expectedSecond() {
        return List.of(new CourtGroupAtHour(CourtGroup.HARD_MEH, LocalTime.parse("18:00")));
    }

    @Override
    List<CourtGroupAtHour> expectedLast() {
        return List.of(
                new CourtGroupAtHour(CourtGroup.HARD_BAD, LocalTime.parse("17:00")),
                new CourtGroupAtHour(CourtGroup.HARD_BAD, LocalTime.parse("20:00")));
    }

    @Override
    List<Integer> getWeightsInOrder() {
        List<Integer> weights = new ArrayList<>();
        IntStream.rangeClosed(1, 30).forEach(weights::add);
        return weights;
    }

    @Override
    Integer getCourtCategoryCount() {
        return 5;
    }

    @Override
    List<Long> getAllRelevantCourtIds() {
        return Court.getIndoorIds();
    }
}