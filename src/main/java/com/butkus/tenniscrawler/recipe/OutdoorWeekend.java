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
 * | clay summer  |   13   |    5   |    1   |    2   |    7   |   13   |   16   |
 * | clay rest    |   14   |    6   |    3   |    4   |    8   |   14   |   17   |
 * | grass        |   15   |   11   |    9   |   10   |   12   |   15   |   18   |
 */
public class OutdoorWeekend extends Recipe {

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
                new CourtGroupAtHour(CourtGroup.CLAY_SUMMER, T1800)
        ));
        map.put(2, List.of(
                new CourtGroupAtHour(CourtGroup.CLAY_SUMMER, T1830)
        ));
        map.put(3, List.of(
                new CourtGroupAtHour(CourtGroup.CLAY_REST, T1800)
        ));
        map.put(4, List.of(
                new CourtGroupAtHour(CourtGroup.CLAY_REST, T1830)
        ));
        map.put(5, List.of(
                new CourtGroupAtHour(CourtGroup.CLAY_SUMMER, T1730)
        ));
        map.put(6, List.of(
                new CourtGroupAtHour(CourtGroup.CLAY_REST, T1730)
        ));
        map.put(7, List.of(
                new CourtGroupAtHour(CourtGroup.CLAY_SUMMER, T1900)
        ));
        map.put(8, List.of(
                new CourtGroupAtHour(CourtGroup.CLAY_REST, T1900)
        ));
        map.put(9, List.of(
                new CourtGroupAtHour(CourtGroup.GRASS, T1800)
        ));
        map.put(10, List.of(
                new CourtGroupAtHour(CourtGroup.GRASS, T1830)
        ));
        map.put(11, List.of(
                new CourtGroupAtHour(CourtGroup.GRASS, T1730)
        ));
        map.put(12, List.of(
                new CourtGroupAtHour(CourtGroup.GRASS, T1900)
        ));
        map.put(13, List.of(
                new CourtGroupAtHour(CourtGroup.CLAY_SUMMER, T1700),
                new CourtGroupAtHour(CourtGroup.CLAY_SUMMER, T1930)
        ));
        map.put(14, List.of(
                new CourtGroupAtHour(CourtGroup.CLAY_REST, T1700),
                new CourtGroupAtHour(CourtGroup.CLAY_REST, T1930)
        ));
        map.put(15, List.of(
                new CourtGroupAtHour(CourtGroup.GRASS, T1700),
                new CourtGroupAtHour(CourtGroup.GRASS, T1930)
        ));
        map.put(16, List.of(
                new CourtGroupAtHour(CourtGroup.CLAY_SUMMER, T2000)
        ));
        map.put(17, List.of(
                new CourtGroupAtHour(CourtGroup.CLAY_REST, T2000)
        ));
        map.put(18, List.of(
                new CourtGroupAtHour(CourtGroup.GRASS, T2000)
        ));
    }

    public OutdoorWeekend() {
        super(map);
    }

    @Override
    public List<Integer> getDurationPreference() {
        return List.of(120, 90, 60);
    }
}
