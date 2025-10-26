package com.butkus.tenniscrawler;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.butkus.tenniscrawler.ExtensionInterest.LATER;
import static com.butkus.tenniscrawler.ExtensionInterest.NONE;

// todo rename maybe. legacy was "Input".
// todo rename other new ones too
public class DesiresExplicit {

    private static final List<Long> IN = Court.getIndoorIds();
    private static final List<Long> CLAY = Court.getClayIds();
    private static final List<Long> OUT = Court.getOutdoorIds();

    private DesiresExplicit() {
    }

    public static List<Desire> makeExplicitDesires() {
        ArrayList<Desire> desires = new ArrayList<>();

        // todo make desires.add("2024-01-01", NONE);
        //  also make explicit holiday acknowledgement, e.g. desires.add("2024-01-01", ANY, Holiday) to force holiday booking; otherwise skip holidays by default
        //  also, in calendar printout at the end, make a note that there's a booking on holiday (or desire on holiday)
        //  this is perhaps a small overkill, but why not do it like Sony and make UX friendly


//        desires.add(new Desire(LocalDate.parse("2025-10-22"), NONE, IN));  // 1900     // fixme: this ANY, NONE, NONE -> yields strike-out date in calendar
//        desires.add(new Desire(LocalDate.parse("2025-10-22"), NONE, CLAY));
//        desires.add(new Desire(LocalDate.parse("2025-10-22"), NONE, OUT));

        desires.add(new Desire(LocalDate.parse("2025-10-26"), NONE, IN));
        desires.add(new Desire(LocalDate.parse("2025-10-26"), NONE, CLAY));
        desires.add(new Desire(LocalDate.parse("2025-10-26"), NONE, OUT));

        desires.add(new Desire(LocalDate.parse("2025-10-29"), LATER, IN));  // 1800
        desires.add(new Desire(LocalDate.parse("2025-10-29"), NONE, CLAY));
        desires.add(new Desire(LocalDate.parse("2025-10-29"), NONE, OUT));

        desires.add(new Desire(LocalDate.parse("2025-11-05"), NONE, IN));  // 1930
        desires.add(new Desire(LocalDate.parse("2025-11-05"), NONE, CLAY));
        desires.add(new Desire(LocalDate.parse("2025-11-05"), NONE, OUT));

        desires.add(new Desire(LocalDate.parse("2025-11-09"), NONE, IN));  // 1830
        desires.add(new Desire(LocalDate.parse("2025-11-09"), NONE, CLAY));
        desires.add(new Desire(LocalDate.parse("2025-11-09"), NONE, OUT));

        desires.add(new Desire(LocalDate.parse("2025-11-12"), NONE, IN));  // 1930
        desires.add(new Desire(LocalDate.parse("2025-11-12"), NONE, CLAY));
        desires.add(new Desire(LocalDate.parse("2025-11-12"), NONE, OUT));
        return desires;
    }
}
