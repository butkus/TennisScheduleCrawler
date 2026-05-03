package com.butkus.tenniscrawler;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Supplier;

@Accessors(chain = true, fluent = true)
@Data
public class MyDesireBuilder {

    private LocalDate date;
    private List<Long> courts;
    private Supplier<Recipe> recipeSupplier;

    public MyDesireBuilder date(LocalDate date) {
        this.date = date;
        return this;
    }

    public MyDesireBuilder courts(List<Long> courts) {
        this.courts = courts;
        return this;
    }

    public MyDesireBuilder recipeSupplier(Supplier<Recipe> recipeSupplier) {
        this.recipeSupplier = recipeSupplier;
        return this;
    }

    public Desire build() {
        if (date == null) throw new IllegalStateException("date must be provided");

        if (recipeSupplier != null) {
            return new Desire(date, recipeSupplier);
        } else if (courts != null) {
            return new Desire(date, courts);
        } else {
            throw new IllegalStateException("either recipeSupplier or courts must be provided");
        }
    }
}
