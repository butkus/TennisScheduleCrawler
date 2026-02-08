package com.butkus.tenniscrawler;

import java.util.ArrayList;
import java.util.List;

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

//      -- SUMMER 3 lines per day example
//        desires.add(new Desire(LocalDate.parse("2025-10-22"), NONE, IN));  // 1900     // fixme: this ANY, NONE, NONE -> yields strike-out date in calendar
//        desires.add(new Desire(LocalDate.parse("2025-10-22"), NONE, CLAY));            // CLAY was a HACK commit, now HACK reverted. CLAY-granularity not yet implemented.
//        desires.add(new Desire(LocalDate.parse("2025-10-22"), NONE, OUT));

        // todo if 2 ordeers are place same day same court (it courrenlty says DuplicateOrdersException: More than 1 indoor Order for 2025-10-29) -- ignore that day

//        desires.add(new Desire(LocalDate.parse("2026-02-08"), NONE, IN));


        return desires;
    }
}
