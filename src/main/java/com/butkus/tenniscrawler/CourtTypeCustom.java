package com.butkus.tenniscrawler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@RequiredArgsConstructor
public enum CourtTypeCustom {
    GRASS("grass", List.of(Court.G1, Court.G2)),
    CLAY_SUMMER("clay summer", List.of(Court.C01, Court.C02, Court.C05, Court.C06)),
    CLAY_REST("clay rest", List.of(Court.C03, Court.C04, Court.C07, Court.C08, Court.C09, Court.C10)),

    HARD("hard", Constants.ALL_HARD_COURTS),
    CARPET("carpet", List.of(Court.K1, Court.K2, Court.K3, Court.K4, Court.K5, Court.K6)),

    HARD_CLOSE("hard close", Constants.HARD_CLOSE_COURTS),
    HARD_FAR("hard far", Constants.HARD_FAR_COURTS),
    CARPET_CLOSE("carpet close", Constants.CARPET_CLOSE_COURTS),
    CARPET_FAR("carpet far", Constants.CARPET_FAR_COURTS);

    private final String name;
    private final Collection<Court> ids;    // todo: in a separate commit: rename to `courts` (and related variable names as well) -- (search for fooFix)

    private static class Constants {
        static final List<Court> ALL_HARD_COURTS;
        static final List<Court> HARD_CLOSE_COURTS;
        static final List<Court> HARD_FAR_COURTS;
        static final List<Court> CARPET_CLOSE_COURTS;
        static final List<Court> CARPET_FAR_COURTS;

        static {
            ALL_HARD_COURTS = new ArrayList<>();
            ALL_HARD_COURTS.add(Court.H01);
            ALL_HARD_COURTS.add(Court.H02);
            ALL_HARD_COURTS.add(Court.H03);
            ALL_HARD_COURTS.add(Court.H04);
            ALL_HARD_COURTS.add(Court.H05);
            ALL_HARD_COURTS.add(Court.H06);
            ALL_HARD_COURTS.add(Court.H07);
            ALL_HARD_COURTS.add(Court.H08);
            ALL_HARD_COURTS.add(Court.H09);
            ALL_HARD_COURTS.add(Court.H10);
            ALL_HARD_COURTS.add(Court.H11);
            ALL_HARD_COURTS.add(Court.H12);
            ALL_HARD_COURTS.add(Court.H13);
            ALL_HARD_COURTS.add(Court.H14);
            ALL_HARD_COURTS.add(Court.H15);
            ALL_HARD_COURTS.add(Court.H16);
            ALL_HARD_COURTS.add(Court.H17);
            ALL_HARD_COURTS.add(Court.H18);
            ALL_HARD_COURTS.add(Court.H19);
            ALL_HARD_COURTS.add(Court.H20);
            ALL_HARD_COURTS.add(Court.H21);
            ALL_HARD_COURTS.add(Court.HCC);

            HARD_CLOSE_COURTS = new ArrayList<>();
            HARD_CLOSE_COURTS.add(Court.H03);
            HARD_CLOSE_COURTS.add(Court.H06);
            HARD_CLOSE_COURTS.add(Court.H09);
            HARD_CLOSE_COURTS.add(Court.H11);
            HARD_CLOSE_COURTS.add(Court.H12);
            HARD_CLOSE_COURTS.add(Court.H14);
            HARD_CLOSE_COURTS.add(Court.H15);
            HARD_CLOSE_COURTS.add(Court.H16);
            HARD_CLOSE_COURTS.add(Court.H17);
            HARD_CLOSE_COURTS.add(Court.H18);
            HARD_CLOSE_COURTS.add(Court.H19);
            HARD_CLOSE_COURTS.add(Court.H20);
            HARD_CLOSE_COURTS.add(Court.H21);

            HARD_FAR_COURTS = new ArrayList<>();
            HARD_FAR_COURTS.add(Court.H01);
            HARD_FAR_COURTS.add(Court.H02);
            HARD_FAR_COURTS.add(Court.H04);
            HARD_FAR_COURTS.add(Court.H05);
            HARD_FAR_COURTS.add(Court.H07);
            HARD_FAR_COURTS.add(Court.H08);
            HARD_FAR_COURTS.add(Court.H10);
            HARD_FAR_COURTS.add(Court.H13);
            HARD_FAR_COURTS.add(Court.HCC);

            CARPET_CLOSE_COURTS = new ArrayList<>();
            CARPET_CLOSE_COURTS.add(Court.K2);
            CARPET_CLOSE_COURTS.add(Court.K3);

            CARPET_FAR_COURTS = new ArrayList<>();
            CARPET_FAR_COURTS.add(Court.K1);
            CARPET_FAR_COURTS.add(Court.K4);
            CARPET_FAR_COURTS.add(Court.K5);
            CARPET_FAR_COURTS.add(Court.K6);
        }

    }
}
