package com.butkus.tenniscrawler;

import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Example table (for visualization purposes):

 *                |  18:00 |  18:30 |  19:00 |  19:30 |  20:00 |
 * |--------------|--------|--------|--------|--------|--------|
 * | grass        |   10   |    9   |    7   |    8   |   10   |
 * | clay summer  |    5   |    4   |    2   |    3   |    5   |
 * | clay rest    |    7   |    6   |    4   |    5   |    7   |
 */
// todo rename to simply OutdoorRecipe --> because indoor+outdoor does not make sense as related.
//  - I either want outdoor (if weather permits) or indoor
//  - are there reasons were I would interchangeably want either/or?
//  - also, currently I don't have the mechanism to allow such freedom. Reasons:
//    - if I want both IN and OUT, I will make 1 desire IN + 1 desire OUT. This is supported.
//    - if I want OutdoorAndIndoor recipe, then I probably need to allow 1 such order/desire per day and nothing else.
//    - these would have to change (not necessarily full list):
//      - DesireMaker (to allow such desires to be added)
//      - DesireOrderPairer (to allow such desires to be paired properly)
//        - now it's allowed to have 1 IN and 1 OUT per day. 2 of OutdoorAndIndoor would make pairing ambiguous
//      - Vacancy pairer (to allow such desires to be processed properly)
public class OutdoorOnlyRecipe extends Recipe {

    public static final LocalTime T1800 = LocalTime.parse("18:00");
    public static final LocalTime T1830 = LocalTime.parse("18:30");
    public static final LocalTime T1900 = LocalTime.parse("19:00");
    public static final LocalTime T1930 = LocalTime.parse("19:30");
    public static final LocalTime T2000 = LocalTime.parse("20:00");

    private static final Map<Integer, List<CourtTypeAtHour>> map;
    static {
        map = new LinkedHashMap<>();
        map.put(2, List.of(
                new CourtTypeAtHour(CourtTypeCustom.CLAY_SUMMER, T1900)
        ));
        map.put(3, List.of(
                new CourtTypeAtHour(CourtTypeCustom.CLAY_SUMMER, T1930)
        ));
        map.put(4, List.of(
                new CourtTypeAtHour(CourtTypeCustom.CLAY_SUMMER, T1830),
                new CourtTypeAtHour(CourtTypeCustom.CLAY_REST, T1900)
        ));
        map.put(5, List.of(
                new CourtTypeAtHour(CourtTypeCustom.CLAY_SUMMER, T1800),
                new CourtTypeAtHour(CourtTypeCustom.CLAY_SUMMER, T2000),
                new CourtTypeAtHour(CourtTypeCustom.CLAY_REST, T1930)
        ));
        map.put(6, List.of(
                new CourtTypeAtHour(CourtTypeCustom.CLAY_REST, T1830)
        ));
        map.put(7, List.of(
                new CourtTypeAtHour(CourtTypeCustom.CLAY_REST, T1800),
                new CourtTypeAtHour(CourtTypeCustom.CLAY_REST, T2000),
                new CourtTypeAtHour(CourtTypeCustom.GRASS, T1900)
        ));
        map.put(8, List.of(
                new CourtTypeAtHour(CourtTypeCustom.GRASS, T1930)
        ));
        map.put(9, List.of(
                new CourtTypeAtHour(CourtTypeCustom.GRASS, T1830)
        ));
        map.put(10, List.of(
                new CourtTypeAtHour(CourtTypeCustom.GRASS, T1800),
                new CourtTypeAtHour(CourtTypeCustom.GRASS, T2000)
        ));
    }

    public OutdoorOnlyRecipe() {
        super(map);
    }

    @Override
    public List<Integer> getDurationPreference() {
        return List.of(90, 60);
    }
}
