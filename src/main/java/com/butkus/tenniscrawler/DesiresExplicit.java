package com.butkus.tenniscrawler;

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

//        desires.add(new Desire(LocalDate.parse("2024-05-09"), NONE, Court.getIndoorIds()));

        desires.add(new Desire("2024-05-15", NONE));
        desires.add(new Desire("2024-05-16", NONE));
        desires.add(new Desire("2024-05-19", NONE));
        desires.add(new Desire("2024-05-20", NONE));
        desires.add(new Desire("2024-05-21", NONE));
        desires.add(new Desire("2024-05-22", NONE));
        desires.add(new Desire("2024-05-23", NONE));
        desires.add(new Desire("2024-05-26", NONE));
        desires.add(new Desire("2024-05-27", NONE));
        desires.add(new Desire("2024-05-28", NONE));
        desires.add(new Desire("2024-05-29", NONE));
        desires.add(new Desire("2024-05-30", NONE));    // would like 1 in SEB (have in bernardinai)

        return desires;
    }
}
