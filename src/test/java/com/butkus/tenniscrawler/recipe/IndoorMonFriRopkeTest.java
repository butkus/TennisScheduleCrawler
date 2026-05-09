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
 * | hard close   |   13   |    9   |    5   |    1   |    9   |
 * | hard far     |   14   |   10   |    6   |    2   |   10   |
 * | carpet close |   15   |   11   |    7   |    3   |   11   |
 * | carpet far   |   16   |   12   |    8   |    4   |   12   |
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
        return List.of(new CourtGroupAtHour(CourtGroup.CARPET_FAR, LocalTime.parse("18:00")));
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