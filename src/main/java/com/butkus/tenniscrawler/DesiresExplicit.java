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

        desires.add(new Desire("2024-04-21", NONE));
        desires.add(new Desire("2024-04-22", NONE));
        desires.add(new Desire("2024-04-23", NONE));      // turim 19:00, tomas gali Balandžio 23 ir 30
        desires.add(new Desire("2024-04-25", LATER));
        desires.add(new Desire("2024-04-28", NONE));

        desires.add(new Desire("2024-05-01", NONE));   // ANY
        desires.add(new Desire("2024-05-02", NONE));
        desires.add(new Desire("2024-05-05", NONE));   // later, weekend (have 1700)
        desires.add(new Desire("2024-05-08", EARLIER));
        desires.add(new Desire("2024-05-12", NONE));
        desires.add(new Desire("2024-05-15", ANY));    // any
        desires.add(new Desire("2024-05-16", NONE));
        desires.add(new Desire("2024-05-19", NONE));
        desires.add(new Desire("2024-05-21", NONE));
        desires.add(new Desire("2024-05-22", ANY));    // any
        desires.add(new Desire("2024-05-26", NONE));
        desires.add(new Desire("2024-05-28", ANY));    // any
        desires.add(new Desire("2024-05-29", EARLIER));

        return desires;
    }
}
