package com.butkus.tenniscrawler.recipe;

import com.butkus.tenniscrawler.Court;
import com.butkus.tenniscrawler.CourtGroupAtHour;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.IntStream;

import static com.butkus.tenniscrawler.CourtGroup.*;

/**
 * Example table (for visualization purposes):

 *                |  18:00 |  18:30 |  19:00 |  19:30 |  20:00 |
 * |--------------|--------|--------|--------|--------|--------|
 * | clay summer  |   13   |    5   |    1   |    3   |   10   |
 * | clay rest    |   14   |    6   |    2   |    4   |   11   |
 * | grass        |   15   |    9   |    7   |    8   |   12   |
 */
class OutdoorMonFriTest extends AbstractRecipeTest {

    @Override
    Recipe getRecipe() {
        return new OutdoorMonFri();
    }

    @Override
    List<CourtGroupAtHour> expectedFirst() {
        return List.of(new CourtGroupAtHour(CLAY_SUMMER, LocalTime.parse("19:00")));
    }

    @Override
    List<CourtGroupAtHour> expectedSecond() {
        return List.of(new CourtGroupAtHour(CLAY_REST, LocalTime.parse("19:00")));
    }

    @Override
    List<CourtGroupAtHour> expectedLast() {
        return List.of(
                new CourtGroupAtHour(GRASS, LocalTime.parse("18:00")));
    }

    @Override
    List<Integer> getWeightsInOrder() {
        return IntStream.rangeClosed(1, 15).boxed().toList();
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