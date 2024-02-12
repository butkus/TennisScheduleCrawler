package com.butkus.tenniscrawler;

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

        desires.add(new Desire("2024-02-11", NONE));
        desires.add(new Desire("2024-02-12", NONE));
        desires.add(new Desire("2024-02-13", NONE));
        desires.add(new Desire("2024-02-14", NONE));
        desires.add(new Desire("2024-02-15", NONE));
        desires.add(new Desire("2024-02-18", NONE));
        desires.add(new Desire("2024-02-19", NONE));
        desires.add(new Desire("2024-02-20", NONE));
        desires.add(new Desire("2024-02-21", NONE));
        desires.add(new Desire("2024-02-22", NONE));
        desires.add(new Desire("2024-02-25", LATER));    // later
        desires.add(new Desire("2024-02-26", EARLIER)); // earlier
        desires.add(new Desire("2024-02-27", LATER));
        desires.add(new Desire("2024-02-28", EARLIER));

        desires.add(new Desire("2024-03-03", LATER));
        desires.add(new Desire("2024-03-04", NONE));
        desires.add(new Desire("2024-03-07", NONE));
        desires.add(new Desire("2024-03-10", NONE));    // later
        desires.add(new Desire("2024-03-17", LATER));
        desires.add(new Desire("2024-03-24", LATER));
        desires.add(new Desire("2024-03-31", LATER));

        desires.add(new Desire("2024-04-01", LATER));
        desires.add(new Desire("2024-04-02", LATER));
        desires.add(new Desire("2024-04-03", LATER));
        desires.add(new Desire("2024-04-04", LATER));
        desires.add(new Desire("2024-04-07", NONE));

        return desires;
    }
}
