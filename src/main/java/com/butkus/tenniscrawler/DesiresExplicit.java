package com.butkus.tenniscrawler;

import java.util.ArrayList;
import java.util.List;

import static com.butkus.tenniscrawler.ExtensionInterest.*;

// todo rename maybe
public class DesiresExplicit {

    private DesiresExplicit() {
    }

    public static List<Desire> makeExplicitDesires() {
        ArrayList<Desire> desires = new ArrayList<>();

        desires.add(new Desire("2024-01-02", NONE));
        desires.add(new Desire("2024-01-03", NONE));
        desires.add(new Desire("2024-01-04", EARLIER));
        desires.add(new Desire("2024-01-08", NONE));
        desires.add(new Desire("2024-01-09", NONE));
        desires.add(new Desire("2024-01-11", LATER));
        desires.add(new Desire("2024-01-16", NONE));
        desires.add(new Desire("2024-01-17", EARLIER));

        desires.add(new Desire("2024-02-14", NONE));
        desires.add(new Desire("2024-02-19", EARLIER));

        return desires;
    }
}
