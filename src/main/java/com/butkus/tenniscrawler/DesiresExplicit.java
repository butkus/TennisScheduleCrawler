package com.butkus.tenniscrawler;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.butkus.tenniscrawler.ExtensionInterest.*;

// todo rename maybe. legacy was "Input".
// todo rename other new ones too
public class DesiresExplicit {

    private DesiresExplicit() {
    }

    public static List<Desire> makeExplicitDesires() {
        ArrayList<Desire> desires = new ArrayList<>();

        // todo make desires.add("2024-01-01", NONE);
        //  also make explicit holiday acknowledgement, e.g. desires.add("2024-01-01", ANY, Holiday) to force holiday booking; otherwise skip holidays by default
        //  also, in calendar printout at the end, make a note that there's a booking on holiday (or desire on holiday)
        //  this is perhaps a small overkill, but why not do it like Sony and make UX friendly

        desires.add(new Desire(LocalDate.parse("2024-07-22"), NONE, Court.getClayIds()));
        desires.add(new Desire(LocalDate.parse("2024-07-23"), NONE, Court.getClayIds()));   // TEMP none, want EARLIER. have 1930 but 1800 too early
        desires.add(new Desire(LocalDate.parse("2024-07-24"), NONE, Court.getClayIds()));
        desires.add(new Desire(LocalDate.parse("2024-07-25"), NONE, Court.getClayIds()));   // TEMP none, want LATER.   have 1800 but 1930 suggestion is late-ish
        desires.add(new Desire(LocalDate.parse("2024-07-28"), LATER, Court.getClayIds()));
        desires.add(new Desire(LocalDate.parse("2024-07-29"), ANY, Court.getClayIds()));
        desires.add(new Desire(LocalDate.parse("2024-07-30"), EARLIER, Court.getClayIds()));

        desires.add(new Desire(LocalDate.parse("2024-08-04"), NONE, Court.getClayIds()));
        desires.add(new Desire(LocalDate.parse("2024-08-05"), NONE, Court.getClayIds()));   // have 1900
        desires.add(new Desire(LocalDate.parse("2024-08-06"), EARLIER, Court.getClayIds()));
        desires.add(new Desire(LocalDate.parse("2024-08-07"), ANY, Court.getClayIds()));
        desires.add(new Desire(LocalDate.parse("2024-08-08"), ANY, Court.getClayIds()));
        desires.add(new Desire(LocalDate.parse("2024-08-11"), NONE, Court.getClayIds()));
        desires.add(new Desire(LocalDate.parse("2024-08-18"), NONE, Court.getClayIds()));
        desires.add(new Desire(LocalDate.parse("2024-08-25"), LATER, Court.getClayIds()));

        return desires;
    }
}
