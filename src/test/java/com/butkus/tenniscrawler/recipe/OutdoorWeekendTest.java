package com.butkus.tenniscrawler.recipe;

import com.butkus.tenniscrawler.Court;
import com.butkus.tenniscrawler.CourtGroupAtHour;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.IntStream;

import static com.butkus.tenniscrawler.CourtGroup.CLAY_SUMMER;
import static com.butkus.tenniscrawler.CourtGroup.GRASS;

/**
 * Example table (for visualization purposes):

 *                |  17:00 |  17:30 |  18:00 |  18:30 |  19:00 |  19:30 |  20:00 |
 * |--------------|--------|--------|--------|--------|--------|--------|--------|
 * | clay summer  |   13   |    5   |    1   |    2   |    7   |   13   |   16   |
 * | clay rest    |   14   |    6   |    3   |    4   |    8   |   14   |   17   |
 * | grass        |   15   |   11   |    9   |   10   |   12   |   15   |   18   |
 */
class OutdoorWeekendTest extends AbstractRecipeTest {

    @Override
    Recipe getRecipe() {
        return new OutdoorWeekend();
    }

    @Override
    List<CourtGroupAtHour> expectedFirst() {
        return List.of(new CourtGroupAtHour(CLAY_SUMMER, LocalTime.parse("18:00")));
    }

    @Override
    List<CourtGroupAtHour> expectedSecond() {
        return List.of(new CourtGroupAtHour(CLAY_SUMMER, LocalTime.parse("18:30")));
    }

    @Override
    List<CourtGroupAtHour> expectedLast() {
        return List.of(
                new CourtGroupAtHour(GRASS, LocalTime.parse("20:00")));
    }

    @Override
    List<Integer> getWeightsInOrder() {
        return IntStream.rangeClosed(1, 18).boxed().toList();
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