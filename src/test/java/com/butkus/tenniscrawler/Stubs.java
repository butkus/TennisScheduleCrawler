package com.butkus.tenniscrawler;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Stubs {

    static List<Desire> stubDesires(String date, ExtensionInterest extensionInterest, List<Long> courtIds) {
        List<Desire> desires = new ArrayList<>();
        desires.add(new Desire(LocalDate.parse(date), extensionInterest, courtIds));
        return desires;
    }

    static List<Desire> stubDesiresRecipe(String date, Recipe recipe) {
        List<Desire> desires = new ArrayList<>();
        desires.add(new Desire(LocalDate.parse(date), recipe));
        return desires;
    }
}
