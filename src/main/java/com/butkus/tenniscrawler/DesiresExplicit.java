package com.butkus.tenniscrawler;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.butkus.tenniscrawler.ExtensionInterest.*;

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


        desires.add(new Desire(LocalDate.parse("2025-06-01"), NONE, IN));  // 1800 2h, Sunday
        desires.add(new Desire(LocalDate.parse("2025-06-01"), NONE, CLAY)); // 1800 2h grass, WANT CLAY
        desires.add(new Desire(LocalDate.parse("2025-06-01"), NONE, OUT)); // 1800 2h grass, WANT CLAY

        desires.add(new Desire(LocalDate.parse("2025-06-04"), NONE, IN));   // 1930 1h hard
        desires.add(new Desire(LocalDate.parse("2025-06-04"), EARLIER, CLAY));  // 2030 1h, WANT EARLIER
        desires.add(new Desire(LocalDate.parse("2025-06-04"), NONE, OUT));  // wednesday, dont need

        desires.add(new Desire(LocalDate.parse("2025-06-06"), NONE, IN));   // 1900 1.5h friday
        desires.add(new Desire(LocalDate.parse("2025-06-06"), NONE, CLAY));  // 1800 1.5h
        desires.add(new Desire(LocalDate.parse("2025-06-06"), NONE, OUT));  // don't want

        desires.add(new Desire(LocalDate.parse("2025-06-08"), NONE, IN));  // 1800 2h
        desires.add(new Desire(LocalDate.parse("2025-06-08"), EARLIER, CLAY)); // 1900 2h, Sunday, WANT EARLIER
        desires.add(new Desire(LocalDate.parse("2025-06-08"), NONE, OUT)); // don't want

        desires.add(new Desire(LocalDate.parse("2025-06-11"), NONE, IN));   // 1930 1h
        desires.add(new Desire(LocalDate.parse("2025-06-11"), NONE, CLAY));  // wednesday, dont need
        desires.add(new Desire(LocalDate.parse("2025-06-11"), NONE, OUT));  // wednesday, dont need

        desires.add(new Desire(LocalDate.parse("2025-06-15"), NONE, IN));   // 1800 2h, Sunday
        desires.add(new Desire(LocalDate.parse("2025-06-15"), LATER, CLAY));  // 1700 2h, WANT LATER
        desires.add(new Desire(LocalDate.parse("2025-06-15"), NONE, OUT));  // don't want

        desires.add(new Desire(LocalDate.parse("2025-06-18"), NONE, IN));   // 1930 1h
        desires.add(new Desire(LocalDate.parse("2025-06-18"), NONE, CLAY));  // wednesday, dont need
        desires.add(new Desire(LocalDate.parse("2025-06-18"), NONE, OUT));  // wednesday, dont need

        desires.add(new Desire(LocalDate.parse("2025-06-22"), NONE, IN));   // 1800 2h
        desires.add(new Desire(LocalDate.parse("2025-06-22"), EARLIER, CLAY)); // 2030 30min, WANT EARLIER
        desires.add(new Desire(LocalDate.parse("2025-06-22"), NONE, OUT));  // don't want

        desires.add(new Desire(LocalDate.parse("2025-06-25"), NONE, IN));   // 1930 1h
        desires.add(new Desire(LocalDate.parse("2025-06-25"), NONE, CLAY));  // wednesday, dont need
        desires.add(new Desire(LocalDate.parse("2025-06-25"), NONE, OUT));  // wednesday, dont need


        return desires;
    }
}
