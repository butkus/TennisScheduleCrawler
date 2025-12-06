package com.butkus.tenniscrawler;

import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Example table (for visualization purposes):

 *                |  18:00 |  18:30 |  19:00 |  19:30 |  20:00 |
 * |--------------|--------|--------|--------|--------|--------|
 * | hard close   |   13   |    9   |    3   |    1   |    9   |
 * | hard far     |   14   |   10   |    4   |    2   |   10   |
 * | carpet close |   15   |   11   |    7   |    5   |   11   |
 * | carpet far   |   16   |   12   |    8   |    6   |   12   |
 */
public class IndoorDetailedRecipe extends Recipe {

    public static final LocalTime T1800 = LocalTime.parse("18:00");
    public static final LocalTime T1830 = LocalTime.parse("18:30");
    public static final LocalTime T1900 = LocalTime.parse("19:00");
    public static final LocalTime T1930 = LocalTime.parse("19:30");
    public static final LocalTime T2000 = LocalTime.parse("20:00");

    private static final Map<Integer, List<CourtTypeAtHour>> map;
    static {
        map = new LinkedHashMap<>();
        map.put(1, List.of(
                new CourtTypeAtHour(CourtTypeCustom.HARD_CLOSE, T1930)
        ));
        map.put(2, List.of(
                new CourtTypeAtHour(CourtTypeCustom.HARD_FAR, T1930)
        ));
        map.put(3, List.of(
                new CourtTypeAtHour(CourtTypeCustom.HARD_CLOSE, T1900)
        ));
        map.put(4, List.of(
                new CourtTypeAtHour(CourtTypeCustom.HARD_FAR, T1900)
        ));
        map.put(5, List.of(
                new CourtTypeAtHour(CourtTypeCustom.CARPET_CLOSE, T1930)
        ));
        map.put(6, List.of(
                new CourtTypeAtHour(CourtTypeCustom.CARPET_FAR, T1930)
        ));
        map.put(7, List.of(
                new CourtTypeAtHour(CourtTypeCustom.CARPET_CLOSE, T1900)
        ));
        map.put(8, List.of(
                new CourtTypeAtHour(CourtTypeCustom.CARPET_FAR, T1900)
        ));
        map.put(9, List.of(
                new CourtTypeAtHour(CourtTypeCustom.HARD_CLOSE, T1830),
                new CourtTypeAtHour(CourtTypeCustom.HARD_CLOSE, T2000)
        ));
        map.put(10, List.of(
                new CourtTypeAtHour(CourtTypeCustom.HARD_FAR, T1830),
                new CourtTypeAtHour(CourtTypeCustom.HARD_FAR, T2000)
        ));
        map.put(11, List.of(
                new CourtTypeAtHour(CourtTypeCustom.CARPET_CLOSE, T1830),
                new CourtTypeAtHour(CourtTypeCustom.CARPET_CLOSE, T2000)
        ));
        map.put(12, List.of(
                new CourtTypeAtHour(CourtTypeCustom.CARPET_FAR, T1830),
                new CourtTypeAtHour(CourtTypeCustom.CARPET_FAR, T2000)
        ));
        map.put(13, List.of(
                new CourtTypeAtHour(CourtTypeCustom.HARD_CLOSE, T1800)
        ));
        map.put(14, List.of(
                new CourtTypeAtHour(CourtTypeCustom.HARD_FAR, T1800)
        ));
        map.put(15, List.of(
                new CourtTypeAtHour(CourtTypeCustom.CARPET_CLOSE, T1800)
        ));
        map.put(16, List.of(
                new CourtTypeAtHour(CourtTypeCustom.CARPET_FAR, T1800)
        ));

    }

    public IndoorDetailedRecipe() {
        super(map);
    }
}
