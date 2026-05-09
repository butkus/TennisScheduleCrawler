package com.butkus.tenniscrawler.recipe;

import com.butkus.tenniscrawler.CourtGroup;
import com.butkus.tenniscrawler.CourtGroupAtHour;

import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
public class IndoorWeekend extends Recipe {

    public static final LocalTime T1700 = LocalTime.parse("17:00");
    public static final LocalTime T1730 = LocalTime.parse("17:30");
    public static final LocalTime T1800 = LocalTime.parse("18:00");
    public static final LocalTime T1830 = LocalTime.parse("18:30");
    public static final LocalTime T1900 = LocalTime.parse("19:00");
    public static final LocalTime T1930 = LocalTime.parse("19:30");
    public static final LocalTime T2000 = LocalTime.parse("20:00");

    private static final Map<Integer, List<CourtGroupAtHour>> map;
    static {
        map = new LinkedHashMap<>();
        map.put(1, List.of(
                new CourtGroupAtHour(CourtGroup.HARD_GOOD, T1800)
        ));
        map.put(2, List.of(
                new CourtGroupAtHour(CourtGroup.HARD_MEH, T1800)
        ));
        map.put(3, List.of(
                new CourtGroupAtHour(CourtGroup.CARPET_CLOSE, T1800)
        ));
        map.put(4, List.of(
                new CourtGroupAtHour(CourtGroup.CARPET_FAR, T1800)
        ));
        map.put(5, List.of(
                new CourtGroupAtHour(CourtGroup.HARD_GOOD, T1830)
        ));
        map.put(6, List.of(
                new CourtGroupAtHour(CourtGroup.HARD_MEH, T1830)
        ));
        map.put(7, List.of(
                new CourtGroupAtHour(CourtGroup.CARPET_CLOSE, T1830)
        ));
        map.put(8, List.of(
                new CourtGroupAtHour(CourtGroup.CARPET_FAR, T1830)
        ));
        map.put(9, List.of(
                new CourtGroupAtHour(CourtGroup.HARD_GOOD, T1900)
        ));
        map.put(10, List.of(
                new CourtGroupAtHour(CourtGroup.HARD_MEH, T1900)
        ));
        map.put(11, List.of(
                new CourtGroupAtHour(CourtGroup.CARPET_CLOSE, T1900)
        ));
        map.put(12, List.of(
                new CourtGroupAtHour(CourtGroup.CARPET_FAR, T1900)
        ));
        map.put(13, List.of(
                new CourtGroupAtHour(CourtGroup.HARD_GOOD, T1730)
        ));
        map.put(14, List.of(
                new CourtGroupAtHour(CourtGroup.HARD_MEH, T1730)
        ));
        map.put(15, List.of(
                new CourtGroupAtHour(CourtGroup.CARPET_CLOSE, T1730)
        ));
        map.put(16, List.of(
                new CourtGroupAtHour(CourtGroup.CARPET_FAR, T1730)
        ));
        map.put(17, List.of(
                new CourtGroupAtHour(CourtGroup.HARD_BAD, T1800)
        ));
        map.put(18, List.of(
                new CourtGroupAtHour(CourtGroup.HARD_BAD, T1830)
        ));
        map.put(19, List.of(
                new CourtGroupAtHour(CourtGroup.HARD_BAD, T1900)
        ));
        map.put(20, List.of(
                new CourtGroupAtHour(CourtGroup.HARD_GOOD, T1930)
        ));
        map.put(21, List.of(
                new CourtGroupAtHour(CourtGroup.HARD_MEH, T1930)
        ));
        map.put(22, List.of(
                new CourtGroupAtHour(CourtGroup.CARPET_CLOSE, T1930)
        ));
        map.put(23, List.of(
                new CourtGroupAtHour(CourtGroup.CARPET_FAR, T1930)
        ));
        map.put(24, List.of(
                new CourtGroupAtHour(CourtGroup.HARD_BAD, T1730)
        ));
        map.put(25, List.of(
                new CourtGroupAtHour(CourtGroup.HARD_BAD, T1930)
        ));
        map.put(26, List.of(
                new CourtGroupAtHour(CourtGroup.HARD_GOOD, T1700),
                new CourtGroupAtHour(CourtGroup.HARD_GOOD, T2000)
        ));
        map.put(27, List.of(
                new CourtGroupAtHour(CourtGroup.HARD_MEH, T1700),
                new CourtGroupAtHour(CourtGroup.HARD_MEH, T2000)
        ));
        map.put(28, List.of(
                new CourtGroupAtHour(CourtGroup.CARPET_CLOSE, T1700),
                new CourtGroupAtHour(CourtGroup.CARPET_CLOSE, T2000)
        ));
        map.put(29, List.of(
                new CourtGroupAtHour(CourtGroup.CARPET_FAR, T1700),
                new CourtGroupAtHour(CourtGroup.CARPET_FAR, T2000)
        ));
        map.put(30, List.of(
                new CourtGroupAtHour(CourtGroup.HARD_BAD, T1700),
                new CourtGroupAtHour(CourtGroup.HARD_BAD, T2000)
        ));
    }

    public IndoorWeekend() {
        super(map);
    }

    @Override
    public List<Integer> getDurationPreference() {
        return List.of(90, 60);
    }
}
