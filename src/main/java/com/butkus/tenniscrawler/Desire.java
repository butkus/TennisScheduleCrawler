package com.butkus.tenniscrawler;

import com.butkus.tenniscrawler.recipe.Recipe;
import com.butkus.tenniscrawler.rest.orders.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Supplier;

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





    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // todo with this approach, I am using lombok's @Builder but augmenting with my own logic
    //      - This leaves lombok's toBuilder() possible to call -- but that would be incorrect
    //      - find a way to construct this with all-native lombok features
    //          - also, calling "build()" on an incomplete builder, should yield an error (e.g. either list of courts must be provided, or a recipe)
    //      - or remove lombok's @Builder completely
    //   read  https://projectlombok.org/features/Builder

    // MADE A NEW MyDesireBuilder CLASS. SAME CONSIDERATIONS ABOVE STILL APPLY.

    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////




    public Desire(LocalDate date, Supplier<Recipe> recipeSupplier) {
        this.date = date;
        this.recipe = recipeSupplier.get();

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
