package com.butkus.tenniscrawler;

import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Example table (for visualization purposes):

 *                |  18:00 |  18:30 |  19:00 |  19:30 |  20:00 |
 * |--------------|--------|--------|--------|--------|--------|
 * | hard         |    4   |    3   |    2   |    1   |    3   |
 * | carpet       |    5   |    4   |    3   |    2   |    4   |
 */
public class IndoorSimpleRecipe extends Recipe {

    public static final LocalTime T1800 = LocalTime.parse("18:00");
    public static final LocalTime T1830 = LocalTime.parse("18:30");
    public static final LocalTime T1900 = LocalTime.parse("19:00");
    public static final LocalTime T1930 = LocalTime.parse("19:30");
    public static final LocalTime T2000 = LocalTime.parse("20:00");

    private static final Map<Integer, List<CourtTypeAtHour>> map;
    static {
        map = new LinkedHashMap<>();
        map.put(1, List.of(
                new CourtTypeAtHour(CourtTypeCustom.HARD, T1930)
        ));
        map.put(2, List.of(
                new CourtTypeAtHour(CourtTypeCustom.HARD, T1900),
                new CourtTypeAtHour(CourtTypeCustom.CARPET, T1930)
        ));
        map.put(3, List.of(
                new CourtTypeAtHour(CourtTypeCustom.HARD, T1830),
                new CourtTypeAtHour(CourtTypeCustom.HARD, T2000),
                new CourtTypeAtHour(CourtTypeCustom.CARPET, T1900)
        ));
        map.put(4, List.of(
                new CourtTypeAtHour(CourtTypeCustom.HARD, T1800),
                new CourtTypeAtHour(CourtTypeCustom.CARPET, T1830),
                new CourtTypeAtHour(CourtTypeCustom.CARPET, T2000)
        ));
        map.put(5, List.of(
                new CourtTypeAtHour(CourtTypeCustom.CARPET, T1800))
        );
    }

    public IndoorSimpleRecipe() {
        super(map);
    }

    @Override
    public List<Integer> getDurationPreference() {
        return List.of(60);
    }
}
