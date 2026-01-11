package com.butkus.tenniscrawler;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.butkus.tenniscrawler.ExtensionInterest.EARLIER;
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

//      -- SUMMER 3 lines per day example
//        desires.add(new Desire(LocalDate.parse("2025-10-22"), NONE, IN));  // 1900     // fixme: this ANY, NONE, NONE -> yields strike-out date in calendar
//        desires.add(new Desire(LocalDate.parse("2025-10-22"), NONE, CLAY));            // CLAY was a HACK commit, now HACK reverted. CLAY-granularity not yet implemented.
//        desires.add(new Desire(LocalDate.parse("2025-10-22"), NONE, OUT));

        //  NEW WAY
        desires.add(new Desire(LocalDate.parse("2026-01-11"), NONE, IN));                       // S 1900
        desires.add(new Desire(LocalDate.parse("2026-01-14"), new IndoorDetailedRecipe()));     // T 1800
        desires.add(new Desire(LocalDate.parse("2026-01-18"), EARLIER, IN));                    // S 1900
        desires.add(new Desire(LocalDate.parse("2026-01-21"), new IndoorDetailedRecipe()));     // T 1800
        desires.add(new Desire(LocalDate.parse("2026-01-25"), EARLIER, IN));                    // S 1900
        desires.add(new Desire(LocalDate.parse("2026-02-01"), EARLIER, IN));                    // S 1900
        desires.add(new Desire(LocalDate.parse("2026-02-04"), new IndoorDetailedRecipe()));     // T 1930
        desires.add(new Desire(LocalDate.parse("2026-02-08"), EARLIER, IN));                    // S 1900
        desires.add(new Desire(LocalDate.parse("2026-02-11"), new IndoorDetailedRecipe()));     // T
        desires.add(new Desire(LocalDate.parse("2026-02-15"), EARLIER, IN));                    // S 1900
        desires.add(new Desire(LocalDate.parse("2026-02-18"), new IndoorDetailedRecipe()));     // T
        desires.add(new Desire(LocalDate.parse("2026-02-22"), EARLIER, IN));                    // S 1900
        desires.add(new Desire(LocalDate.parse("2026-03-01"), EARLIER, IN));                    // S 1900
        desires.add(new Desire(LocalDate.parse("2026-03-08"), EARLIER, IN));                    // S 1900
        desires.add(new Desire(LocalDate.parse("2026-03-15"), EARLIER, IN));                    // S 1900
        desires.add(new Desire(LocalDate.parse("2026-03-22"), EARLIER, IN));                    // S 1830 K2
        desires.add(new Desire(LocalDate.parse("2026-03-29"), EARLIER, IN));                    // S 1930 K2
        desires.add(new Desire(LocalDate.parse("2026-04-01"), new IndoorDetailedRecipe()));     // T 1800
        desires.add(new Desire(LocalDate.parse("2026-04-05"), EARLIER, IN));                    // S 1830

         // OLD WAY
//        desires.add(new Desire(LocalDate.parse("2026-01-11"), NONE, IN));  // 1900
//        desires.add(new Desire(LocalDate.parse("2026-01-14"), LATER, IN));  // 1800
//        desires.add(new Desire(LocalDate.parse("2026-01-18"), EARLIER, IN));  // 1900
//        desires.add(new Desire(LocalDate.parse("2026-01-21"), LATER, IN));  // 1800
//        desires.add(new Desire(LocalDate.parse("2026-01-25"), EARLIER, IN));  // 1900
//        desires.add(new Desire(LocalDate.parse("2026-02-01"), EARLIER, IN));  // 1900
//        desires.add(new Desire(LocalDate.parse("2026-02-04"), NONE, IN));  // 1930
//        desires.add(new Desire(LocalDate.parse("2026-02-08"), EARLIER, IN));  // 1900
//        desires.add(new Desire(LocalDate.parse("2026-02-11"), NONE, IN));
//        desires.add(new Desire(LocalDate.parse("2026-02-15"), EARLIER, IN));  // 1900
//        desires.add(new Desire(LocalDate.parse("2026-02-18"), NONE, IN));
//        desires.add(new Desire(LocalDate.parse("2026-02-22"), EARLIER, IN));  // 1900
//        desires.add(new Desire(LocalDate.parse("2026-03-01"), EARLIER, IN));  // 1900
//        desires.add(new Desire(LocalDate.parse("2026-03-08"), EARLIER, IN));  // 1900
//        desires.add(new Desire(LocalDate.parse("2026-03-15"), EARLIER, IN));  // 1900
//        desires.add(new Desire(LocalDate.parse("2026-03-22"), EARLIER, IN));  // 1830 K2
//        desires.add(new Desire(LocalDate.parse("2026-03-29"), EARLIER, IN));  // 1930 K2
//        desires.add(new Desire(LocalDate.parse("2026-04-01"), LATER, IN));  // 1800
//        desires.add(new Desire(LocalDate.parse("2026-04-05"), EARLIER, IN));  // 1830 sekm

        return desires;
    }
}
