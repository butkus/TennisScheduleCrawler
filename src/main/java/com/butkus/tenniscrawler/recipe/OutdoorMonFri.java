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
 * | clay summer  |   13   |    5   |    1   |    3   |   10   |
 * | clay rest    |   14   |    6   |    2   |    4   |   11   |
 * | grass        |   15   |    9   |    7   |    8   |   12   |
 */
public class OutdoorMonFri extends Recipe {

    public static final LocalTime T1800 = LocalTime.parse("18:00");
    public static final LocalTime T1830 = LocalTime.parse("18:30");
    public static final LocalTime T1900 = LocalTime.parse("19:00");
    public static final LocalTime T1930 = LocalTime.parse("19:30");
    public static final LocalTime T2000 = LocalTime.parse("20:00");

    private static final Map<Integer, List<CourtGroupAtHour>> map;
    static {
        map = new LinkedHashMap<>();
        map.put(1, List.of(
                new CourtGroupAtHour(CourtGroup.CLAY_SUMMER, T1900)
        ));
        map.put(2, List.of(
                new CourtGroupAtHour(CourtGroup.CLAY_REST, T1900)
        ));
        map.put(3, List.of(
                new CourtGroupAtHour(CourtGroup.CLAY_SUMMER, T1930)
        ));
        map.put(4, List.of(
                new CourtGroupAtHour(CourtGroup.CLAY_REST, T1930)
        ));
        map.put(5, List.of(
                new CourtGroupAtHour(CourtGroup.CLAY_SUMMER, T1830)
        ));
        map.put(6, List.of(
                new CourtGroupAtHour(CourtGroup.CLAY_REST, T1830)
        ));
        map.put(7, List.of(
                new CourtGroupAtHour(CourtGroup.GRASS, T1900)
        ));
        map.put(8, List.of(
                new CourtGroupAtHour(CourtGroup.GRASS, T1930)
        ));
        map.put(9, List.of(
                new CourtGroupAtHour(CourtGroup.GRASS, T1830)
        ));
        map.put(10, List.of(
                new CourtGroupAtHour(CourtGroup.CLAY_SUMMER, T2000)
        ));
        map.put(11, List.of(
                new CourtGroupAtHour(CourtGroup.CLAY_REST, T2000)
        ));
        map.put(12, List.of(
                new CourtGroupAtHour(CourtGroup.GRASS, T2000)
        ));
        map.put(13, List.of(
                new CourtGroupAtHour(CourtGroup.CLAY_SUMMER, T1800)
        ));
        map.put(14, List.of(
                new CourtGroupAtHour(CourtGroup.CLAY_REST, T1800)
        ));
        map.put(15, List.of(
                new CourtGroupAtHour(CourtGroup.GRASS, T1800)
        ));
    }

    public OutdoorMonFri() {
        super(map);
    }

    @Override
    public List<Integer> getDurationPreference() {
        return List.of(90, 60);
    }
}
