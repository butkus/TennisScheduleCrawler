package com.butkus.tenniscrawler;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.butkus.tenniscrawler.ExtensionInterest.NONE;

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


        desires.add(new Desire(LocalDate.parse("2025-05-04"), NONE, Court.getIndoorIds()));
        desires.add(new Desire(LocalDate.parse("2025-05-06"), NONE, Court.getIndoorIds()));
        desires.add(new Desire(LocalDate.parse("2025-05-07"), NONE, Court.getIndoorIds()));
        desires.add(new Desire(LocalDate.parse("2025-05-08"), NONE, Court.getIndoorIds()));
        desires.add(new Desire(LocalDate.parse("2025-05-11"), NONE, Court.getIndoorIds()));
        desires.add(new Desire(LocalDate.parse("2025-05-13"), NONE, Court.getIndoorIds()));
        desires.add(new Desire(LocalDate.parse("2025-05-14"), NONE, Court.getIndoorIds()));
        desires.add(new Desire(LocalDate.parse("2025-05-15"), NONE, Court.getIndoorIds()));
        desires.add(new Desire(LocalDate.parse("2025-05-18"), NONE, Court.getIndoorIds()));
        desires.add(new Desire(LocalDate.parse("2025-05-20"), NONE, Court.getIndoorIds()));
        desires.add(new Desire(LocalDate.parse("2025-05-21"), NONE, Court.getIndoorIds()));
        desires.add(new Desire(LocalDate.parse("2025-05-22"), NONE, Court.getIndoorIds()));
        desires.add(new Desire(LocalDate.parse("2025-05-25"), NONE, Court.getIndoorIds()));
        desires.add(new Desire(LocalDate.parse("2025-05-27"), NONE, Court.getIndoorIds()));
        desires.add(new Desire(LocalDate.parse("2025-05-28"), NONE, Court.getIndoorIds()));
        desires.add(new Desire(LocalDate.parse("2025-05-29"), NONE, Court.getIndoorIds()));

        desires.add(new Desire(LocalDate.parse("2025-06-01"), NONE, Court.getIndoorIds()));

        return desires;
    }
}
