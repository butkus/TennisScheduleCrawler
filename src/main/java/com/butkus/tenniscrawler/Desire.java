package com.butkus.tenniscrawler;

import com.butkus.tenniscrawler.rest.orders.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Builder(toBuilder = true)
@Data
public class Desire {

    private LocalDate date;
    private Order order;

    // OLD
    private ExtensionInterest extensionInterest;
    private List<Long> courts;

    // NEW -- extensionInterest + courts --> one single Recipe
    private Recipe recipe;


    public Desire(LocalDate date, Recipe recipe) {
        this.date = date;
        this.recipe = recipe;

        this.courts = recipe.getCourtIds(); // todo after OLD way is decomissioned, this.courts will be redundant (it is alredy now, but kept for backward compatibility)
    }

    public Desire(LocalDate date, ExtensionInterest extensionInterest, List<Long> courts) {
        this.date = date;
        this.extensionInterest = extensionInterest;
        this.courts = courts;
    }

    // fixme for summer, default courts should be indoors + outdoors. Or perhaps treat them separatelly, but then make 2 constructors (or non-constructor methods) for each
    public Desire(String date) {
        this.date = LocalDate.parse(date);
        this.extensionInterest = ExtensionInterest.ANY;
        this.courts = Court.getIndoorIds();
    }

    // fixme for summer, default courts should be indoors + outdoors. Or perhaps treat them separatelly, but then make 2 constructors (or non-constructor methods) for each
    public Desire(LocalDate date, List<Long> courts) {
        this.date = date;
        this.extensionInterest = ExtensionInterest.ANY;
        this.courts = courts;
    }

    // fixme for summer, default courts should be indoors + outdoors. Or perhaps treat them separatelly, but then make 2 constructors (or non-constructor methods) for each
    public Desire(String date, ExtensionInterest extensionInterest) {
        this.date = LocalDate.parse(date);
        this.extensionInterest = extensionInterest;
        this.courts = Court.getIndoorIds();  // fixme: (ctrl-f FOO1 for 2 identical comments): related
    }
}
