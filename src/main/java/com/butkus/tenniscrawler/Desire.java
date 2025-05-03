package com.butkus.tenniscrawler;

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
    private ExtensionInterest extensionInterest;

    private List<Long> courts;
    private List<Long> alternativeCourts;
    private boolean useRedundantBooking;

    public Desire(LocalDate date, ExtensionInterest extensionInterest, List<Long> courts) {
        this.date = date;
        this.extensionInterest = extensionInterest;
        this.courts = courts;
    }

    public Desire(LocalDate date, List<Long> courts, List<Long> alternativeCourts) {
        this.date = date;
        this.extensionInterest = ExtensionInterest.ANY;
        this.courts = courts;
        this.alternativeCourts = alternativeCourts;
        this.useRedundantBooking = true;
    }

    // fixme for summer, default courts should be indoors + outdoors. Or perhaps treat them separatelly, but then make 2 constructors (or non-constructor methods) for each
    public Desire(String date) {
        this.date = LocalDate.parse(date);
        this.extensionInterest = ExtensionInterest.ANY;
        this.courts = Court.getIndoorIds();
    }

    // fixme for summer, default courts should be indoors + outdoors. Or perhaps treat them separatelly, but then make 2 constructors (or non-constructor methods) for each
    public Desire(LocalDate date) {
        this.date = date;
        this.extensionInterest = ExtensionInterest.ANY;
        this.courts = Court.getIds();
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
