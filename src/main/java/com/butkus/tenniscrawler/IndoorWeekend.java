package com.butkus.tenniscrawler;

import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Example table (for visualization purposes):

 *                |  17:00 |  17:30 |  18:00 |  18:30 |  19:00 |  19:30 |  20:00 |
 * |--------------|--------|--------|--------|--------|--------|--------|--------|
 * | hard close   |   21   |   13   |    1   |    5   |    9   |   17   |   21   |
 * | hard far     |   22   |   14   |    2   |    6   |   10   |   18   |   22   |
 * | carpet close |   23   |   15   |    3   |    7   |   11   |   19   |   23   |
 * | carpet far   |   24   |   16   |    4   |    8   |   12   |   20   |   24   |
 */
public class IndoorWeekend extends Recipe {

    public static final LocalTime T1700 = LocalTime.parse("17:00");
    public static final LocalTime T1730 = LocalTime.parse("17:30");
    public static final LocalTime T1800 = LocalTime.parse("18:00");
    public static final LocalTime T1830 = LocalTime.parse("18:30");
    public static final LocalTime T1900 = LocalTime.parse("19:00");
    public static final LocalTime T1930 = LocalTime.parse("19:30");
    public static final LocalTime T2000 = LocalTime.parse("20:00");

    private static final Map<Integer, List<CourtTypeAtHour>> map;
    static {
        map = new LinkedHashMap<>();
        map.put(1, List.of(
                new CourtTypeAtHour(CourtTypeCustom.HARD_CLOSE, T1800)
        ));
        map.put(2, List.of(
                new CourtTypeAtHour(CourtTypeCustom.HARD_FAR, T1800)
        ));
        map.put(3, List.of(
                new CourtTypeAtHour(CourtTypeCustom.CARPET_CLOSE, T1800)
        ));
        map.put(4, List.of(
                new CourtTypeAtHour(CourtTypeCustom.CARPET_FAR, T1800)
        ));
        map.put(5, List.of(
                new CourtTypeAtHour(CourtTypeCustom.HARD_CLOSE, T1830)
        ));
        map.put(6, List.of(
                new CourtTypeAtHour(CourtTypeCustom.HARD_FAR, T1830)
        ));
        map.put(7, List.of(
                new CourtTypeAtHour(CourtTypeCustom.CARPET_CLOSE, T1830)
        ));
        map.put(8, List.of(
                new CourtTypeAtHour(CourtTypeCustom.CARPET_FAR, T1830)
        ));
        map.put(9, List.of(
                new CourtTypeAtHour(CourtTypeCustom.HARD_CLOSE, T1900)
        ));
        map.put(10, List.of(
                new CourtTypeAtHour(CourtTypeCustom.HARD_FAR, T1900)
        ));
        map.put(11, List.of(
                new CourtTypeAtHour(CourtTypeCustom.CARPET_CLOSE, T1900)
        ));
        map.put(12, List.of(
                new CourtTypeAtHour(CourtTypeCustom.CARPET_FAR, T1900)
        ));
        map.put(13, List.of(
                new CourtTypeAtHour(CourtTypeCustom.HARD_CLOSE, T1730)
        ));
        map.put(14, List.of(
                new CourtTypeAtHour(CourtTypeCustom.HARD_FAR, T1730)
        ));
        map.put(15, List.of(
                new CourtTypeAtHour(CourtTypeCustom.CARPET_CLOSE, T1730)
        ));
        map.put(16, List.of(
                new CourtTypeAtHour(CourtTypeCustom.CARPET_FAR, T1730)
        ));
        map.put(17, List.of(
                new CourtTypeAtHour(CourtTypeCustom.HARD_CLOSE, T1930)
        ));
        map.put(18, List.of(
                new CourtTypeAtHour(CourtTypeCustom.HARD_FAR, T1930)
        ));
        map.put(19, List.of(
                new CourtTypeAtHour(CourtTypeCustom.CARPET_CLOSE, T1930)
        ));
        map.put(20, List.of(
                new CourtTypeAtHour(CourtTypeCustom.CARPET_FAR, T1930)
        ));
        map.put(21, List.of(
                new CourtTypeAtHour(CourtTypeCustom.HARD_CLOSE, T1700),
                new CourtTypeAtHour(CourtTypeCustom.HARD_CLOSE, T2000)
        ));
        map.put(22, List.of(
                new CourtTypeAtHour(CourtTypeCustom.HARD_FAR, T1700),
                new CourtTypeAtHour(CourtTypeCustom.HARD_FAR, T2000)
        ));
        map.put(23, List.of(
                new CourtTypeAtHour(CourtTypeCustom.CARPET_CLOSE, T1700),
                new CourtTypeAtHour(CourtTypeCustom.CARPET_CLOSE, T2000)
        ));
        map.put(24, List.of(
                new CourtTypeAtHour(CourtTypeCustom.CARPET_FAR, T1700),
                new CourtTypeAtHour(CourtTypeCustom.CARPET_FAR, T2000)
        ));

    }

    public IndoorWeekend() {
        super(map);
    }


    // FIXME: return List.of(90, 60);   --> i have fixed it already w/o tests. do I need tests? It's tested separately.
    @Override
    public List<Integer> getDurationPreference() {
        return List.of(90, 60);
    }
}
