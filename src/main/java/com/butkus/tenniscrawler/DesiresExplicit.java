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

        desires.add(new Desire(LocalDate.parse("2025-02-09"), NONE, Court.getIndoorIds()));
        desires.add(new Desire(LocalDate.parse("2025-02-11"), NONE, Court.getIndoorIds()));
        desires.add(new Desire(LocalDate.parse("2025-02-12"), NONE, Court.getIndoorIds()));
        desires.add(new Desire(LocalDate.parse("2025-02-13"), NONE, Court.getIndoorIds()));
        desires.add(new Desire(LocalDate.parse("2025-02-18"), NONE, Court.getIndoorIds()));
        desires.add(new Desire(LocalDate.parse("2025-02-19"), NONE, Court.getIndoorIds()));
        desires.add(new Desire(LocalDate.parse("2025-02-20"), LATER, Court.getIndoorIds()));
        desires.add(new Desire(LocalDate.parse("2025-02-23"), NONE, Court.getIndoorIds()));
        desires.add(new Desire(LocalDate.parse("2025-02-25"), LATER, Court.getIndoorIds()));
        desires.add(new Desire(LocalDate.parse("2025-02-26"), NONE, Court.getIndoorIds()));

        desires.add(new Desire(LocalDate.parse("2025-03-02"), NONE, Court.getIndoorIds()));
        desires.add(new Desire(LocalDate.parse("2025-03-04"), NONE, Court.getIndoorIds()));
        desires.add(new Desire(LocalDate.parse("2025-03-05"), LATER, Court.getIndoorIds()));
        desires.add(new Desire(LocalDate.parse("2025-03-06"), LATER, Court.getIndoorIds()));
        desires.add(new Desire(LocalDate.parse("2025-03-09"), NONE, Court.getIndoorIds()));
        desires.add(new Desire(LocalDate.parse("2025-03-12"), NONE, Court.getIndoorIds()));
        desires.add(new Desire(LocalDate.parse("2025-03-30"), NONE, Court.getIndoorIds()));

        desires.add(new Desire(LocalDate.parse("2025-04-20"), EARLIER, Court.getIndoorIds()));

        return desires;
    }
}
