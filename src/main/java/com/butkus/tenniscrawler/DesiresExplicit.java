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
        desires.add(new Desire("2024-01-02", NONE));
        desires.add(new Desire("2024-01-03", NONE));
        desires.add(new Desire("2024-01-04", EARLIER));
        desires.add(new Desire("2024-01-08", NONE));
        desires.add(new Desire("2024-01-09", NONE));
        desires.add(new Desire("2024-01-10", ANY));
        desires.add(new Desire("2024-01-11", LATER));
        desires.add(new Desire("2024-01-16", NONE));
        desires.add(new Desire("2024-01-17", EARLIER));

        desires.add(new Desire("2024-02-14", NONE));
        desires.add(new Desire("2024-02-19", EARLIER));

        return desires;
    }
}
