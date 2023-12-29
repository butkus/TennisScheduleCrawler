package com.butkus.tenniscrawler;

import lombok.RequiredArgsConstructor;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class DesireMaker {

    private final Clock clock;

    private List<Desire> explicitDesires = new ArrayList<>();
    private List<Desire> periodicDesires = new ArrayList<>();

    public void reset() {
        this.explicitDesires = new ArrayList<>();
        this.periodicDesires = new ArrayList<>();
    }

    public List<Desire> make() {
        this.explicitDesires.sort(Comparator.comparing(Desire::getDate));
        this.periodicDesires.sort(Comparator.comparing(Desire::getDate));

        List<Desire> combined = new ArrayList<>();
        List<LocalDate> explicitDates = explicitDesires.stream()    // todo do we need to sort? in the end the list is sorted anyways. (add test in DesireMakerTest)
                .map(Desire::getDate).collect(Collectors.toList());
        for (Desire periodicDesire : periodicDesires) {
            if (!explicitDates.contains(periodicDesire.getDate())) {
                combined.add(periodicDesire);
            }
        }
        combined.addAll(explicitDesires);
        combined.sort(Comparator.comparing(Desire::getDate));
        return combined;
    }

    public DesireMaker addExplicitDesires() {
        this.explicitDesires = DesiresExplicit.makeExplicitDesires();
        return this;
    }

    public DesireMaker addNext(int count, DayOfWeek dayOfWeek) {
        LocalDate startDate = getNow();
        List<Desire> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            LocalDate next = startDate.with(TemporalAdjusters.next(dayOfWeek));
            result.add(new Desire(next));
            startDate = next;
        }
        this.periodicDesires.addAll(result);
        return this;
    }

    private LocalDate getNow() {
        return LocalDate.now(clock);
    }

}
