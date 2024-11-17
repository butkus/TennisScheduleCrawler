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

        desires.add(new Desire(LocalDate.parse("2024-11-17"), NONE, Court.getIndoorIds()));
        desires.add(new Desire(LocalDate.parse("2024-11-19"), NONE, Court.getIndoorIds()));
        desires.add(new Desire(LocalDate.parse("2024-11-21"), EARLIER, Court.getSquashIds()));      // SQUASH
        desires.add(new Desire(LocalDate.parse("2024-11-24"), NONE, Court.getIndoorIds()));
        desires.add(new Desire(LocalDate.parse("2024-11-26"), LATER, Court.getIndoorIds()));

        desires.add(new Desire(LocalDate.parse("2024-12-10"), EARLIER, Court.getIndoorIds()));
        desires.add(new Desire(LocalDate.parse("2024-12-22"), NONE, Court.getIndoorIds()));
        desires.add(new Desire(LocalDate.parse("2024-12-29"), NONE, Court.getIndoorIds()));

        desires.add(new Desire(LocalDate.parse("2025-01-02"), LATER, Court.getIndoorIds()));

        return desires;
    }
}
