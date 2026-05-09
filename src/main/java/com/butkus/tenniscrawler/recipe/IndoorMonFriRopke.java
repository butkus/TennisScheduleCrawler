package com.butkus.tenniscrawler.recipe;

import com.butkus.tenniscrawler.CourtGroup;
import com.butkus.tenniscrawler.CourtGroupAtHour;

import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
public class IndoorMonFriRopke extends Recipe {

    public static final LocalTime T1800 = LocalTime.parse("18:00");
    public static final LocalTime T1830 = LocalTime.parse("18:30");
    public static final LocalTime T1900 = LocalTime.parse("19:00");
    public static final LocalTime T1930 = LocalTime.parse("19:30");
    public static final LocalTime T2000 = LocalTime.parse("20:00");

    private static final Map<Integer, List<CourtGroupAtHour>> map;
    static {
        map = new LinkedHashMap<>();
        map.put(1, List.of(
                new CourtGroupAtHour(CourtGroup.HARD_GOOD, T1930)
        ));
        map.put(2, List.of(
                new CourtGroupAtHour(CourtGroup.HARD_MEH, T1930)
        ));
        map.put(3, List.of(
                new CourtGroupAtHour(CourtGroup.CARPET_CLOSE, T1930)
        ));
        map.put(4, List.of(
                new CourtGroupAtHour(CourtGroup.CARPET_FAR, T1930)
        ));
        map.put(5, List.of(
                new CourtGroupAtHour(CourtGroup.HARD_BAD, T1930)
        ));
        map.put(6, List.of(
                new CourtGroupAtHour(CourtGroup.HARD_GOOD, T1900)
        ));
        map.put(7, List.of(
                new CourtGroupAtHour(CourtGroup.HARD_MEH, T1900)
        ));
        map.put(8, List.of(
                new CourtGroupAtHour(CourtGroup.CARPET_CLOSE, T1900)
        ));
        map.put(9, List.of(
                new CourtGroupAtHour(CourtGroup.CARPET_FAR, T1900)
        ));
        map.put(10, List.of(
                new CourtGroupAtHour(CourtGroup.HARD_GOOD, T2000)
        ));
        map.put(11, List.of(
                new CourtGroupAtHour(CourtGroup.HARD_MEH, T2000)
        ));
        map.put(12, List.of(
                new CourtGroupAtHour(CourtGroup.CARPET_CLOSE, T2000)
        ));
        map.put(13, List.of(
                new CourtGroupAtHour(CourtGroup.CARPET_FAR, T2000)
        ));
        map.put(14, List.of(
                new CourtGroupAtHour(CourtGroup.HARD_BAD, T1900)
        ));
        map.put(15, List.of(
                new CourtGroupAtHour(CourtGroup.HARD_BAD, T2000)
        ));
        map.put(16, List.of(
                new CourtGroupAtHour(CourtGroup.HARD_GOOD, T1830)
        ));
        map.put(17, List.of(
                new CourtGroupAtHour(CourtGroup.HARD_MEH, T1830)
        ));
        map.put(18, List.of(
                new CourtGroupAtHour(CourtGroup.CARPET_CLOSE, T1830)
        ));
        map.put(19, List.of(
                new CourtGroupAtHour(CourtGroup.CARPET_FAR, T1830)
        ));
        map.put(20, List.of(
                new CourtGroupAtHour(CourtGroup.HARD_BAD, T1830)
        ));
        map.put(21, List.of(
                new CourtGroupAtHour(CourtGroup.HARD_GOOD, T1800)
        ));
        map.put(22, List.of(
                new CourtGroupAtHour(CourtGroup.HARD_MEH, T1800)
        ));
        map.put(23, List.of(
                new CourtGroupAtHour(CourtGroup.CARPET_CLOSE, T1800)
        ));
        map.put(24, List.of(
                new CourtGroupAtHour(CourtGroup.CARPET_FAR, T1800)
        ));
        map.put(25, List.of(
                new CourtGroupAtHour(CourtGroup.HARD_BAD, T1800)
        ));

    }

    public IndoorMonFriRopke() {
        super(map);
    }

    @Override
    public List<Integer> getDurationPreference() {
        return List.of(60);
    }
}
