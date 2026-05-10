package com.butkus.tenniscrawler.recipe;

import com.butkus.tenniscrawler.Court;
import com.butkus.tenniscrawler.CourtGroup;
import com.butkus.tenniscrawler.CourtGroupAtHour;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Example table (for visualization purposes):

 *                |  18:00 |  18:30 |  19:00 |  19:30 |  20:00 |
 * |--------------|--------|--------|--------|--------|--------|
 * | hard good    |   21   |   16   |    3   |    1   |    9   |
 * | hard meh     |   22   |   17   |    4   |    2   |   10   |
 * | carpet close |   23   |   18   |    7   |    5   |   11   |
 * | carpet far   |   24   |   19   |    8   |    6   |   12   |
 * | hard bad     |   25   |   20   |   14   |   13   |   15   |
 */
class IndoorMonFriTest extends AbstractRecipeTest {

    @Override
    Recipe getRecipe() {
        return new IndoorMonFri();
    }

    @Override
    List<CourtGroupAtHour> expectedFirst() {
        return List.of(new CourtGroupAtHour(CourtGroup.HARD_GOOD, LocalTime.parse("19:30")));
    }

    @Override
    List<CourtGroupAtHour> expectedSecond() {
        return List.of(new CourtGroupAtHour(CourtGroup.HARD_MEH, LocalTime.parse("19:30")));
    }

    @Override
    List<CourtGroupAtHour> expectedLast() {
        return List.of(new CourtGroupAtHour(CourtGroup.HARD_BAD, LocalTime.parse("18:00")));
    }

    @Override
    List<Integer> getWeightsInOrder() {
        return IntStream.rangeClosed(1, 25).boxed().toList();
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