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

        validateDesires(periodicDesires);
        validateDesires(explicitDesires);

        List<Desire> combined = new ArrayList<>();
        List<LocalDate> explicitDates = explicitDesires.stream().map(Desire::getDate).collect(Collectors.toList());
        for (Desire periodicDesire : periodicDesires) {
            LocalDate date = periodicDesire.getDate();
            boolean notInExplicitDesires = !explicitDates.contains(date);
            if (notInExplicitDesires && !isHoliday(date)) {
                combined.add(periodicDesire);
            }
        }
        combined.addAll(explicitDesires);
        combined.sort(Comparator.comparing(Desire::getDate));
        return combined;
    }

    // todo remove redundant desire infra
    private void validateDesires(List<Desire> desires) {
        boolean anyDateHasMoreThan2Desires = desires.stream().collect(Collectors.groupingBy(Desire::getDate, Collectors.counting()))
                .values().stream().anyMatch(count -> count > 2);
        if (anyDateHasMoreThan2Desires) throw new DuplicateDesiresException();
    }

    public DesireMaker addExplicitDesires() {
        this.explicitDesires = DesiresExplicit.makeExplicitDesires();
        return this;
    }

    public DesireMaker addNext(int count, DayOfWeek dayOfWeek) {
        Desire regularDesireDraft = new Desire(getNow(), Court.getIndoorIds());  // todo write a test  // fixme: (ctrl-f FOO1 for 2 identical comments): related
        return addNext(count, dayOfWeek, regularDesireDraft);
    }

    public DesireMaker addNextRedundant(int count, DayOfWeek dayOfWeek, List<Long> mainCourts, List<Long> alternativeCourts) {
        Desire redundantDesireDraft = new Desire(getNow(), mainCourts, alternativeCourts);
        return addNext(count, dayOfWeek, redundantDesireDraft);
    }

    public DesireMaker addNext(int count, DayOfWeek dayOfWeek, Desire draft) {
        LocalDate startDate = getNow();
        List<Desire> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            LocalDate next = startDate.with(TemporalAdjusters.next(dayOfWeek));
            result.add(cloneWithDateChange(draft, next));
            startDate = next;
        }
        this.periodicDesires.addAll(result);
        return this;
    }

    private static Desire cloneWithDateChange(Desire desire, LocalDate date) {
        return desire.toBuilder().date(date).build();
    }

    private LocalDate getNow() {
        return LocalDate.now(clock);
    }

    private boolean isHoliday(LocalDate date) {
        return getFixedHolidays().contains(date);
    }

    public List<LocalDate> getFixedHolidays() {
        int thisYear = getNow().getYear();
        int nextYear = thisYear + 1;

        List<LocalDate> holidays = new ArrayList<>();
        addFixedHolidays(holidays, thisYear);
        addFixedHolidays(holidays, nextYear);

        return holidays;
    }

    private void addFixedHolidays(List<LocalDate> holidays, int year) {
        holidays.add(LocalDate.of(year, 1, 1));
        holidays.add(LocalDate.of(year, 2, 16));
        holidays.add(LocalDate.of(year, 3, 11));
        holidays.add(LocalDate.of(year, 5, 1));
        holidays.add(LocalDate.of(year, 6, 24));
        holidays.add(LocalDate.of(year, 7, 6));
        holidays.add(LocalDate.of(year, 8, 15));
        holidays.add(LocalDate.of(year, 11, 1));
        holidays.add(LocalDate.of(year, 11, 2));
        holidays.add(LocalDate.of(year, 12, 24));
        holidays.add(LocalDate.of(year, 12, 25));
        holidays.add(LocalDate.of(year, 12, 26));
    }

}
