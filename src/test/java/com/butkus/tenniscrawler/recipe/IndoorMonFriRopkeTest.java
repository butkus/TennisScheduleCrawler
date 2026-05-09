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

 *                |  18:00 |  18:30 |  19:00 |  19:30 |  20:00 |
 * |--------------|--------|--------|--------|--------|--------|
 * | hard good    |   21   |   16   |    6   |    1   |   10   |
 * | hard meh     |   22   |   17   |    7   |    2   |   11   |
 * | carpet close |   23   |   18   |    8   |    3   |   12   |
 * | carpet far   |   24   |   19   |    9   |    4   |   13   |
 * | hard bad     |   25   |   20   |   14   |    5   |   15   |
 */
class IndoorMonFriRopkeTest extends AbstractRecipeTest {

    @Override
    Recipe getRecipe() {
        return new IndoorMonFriRopke();
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
        List<Integer> weights = new ArrayList<>();
        IntStream.rangeClosed(1, 25).forEach(weights::add);
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