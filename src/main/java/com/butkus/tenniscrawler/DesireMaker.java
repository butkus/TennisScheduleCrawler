package com.butkus.tenniscrawler;

import lombok.RequiredArgsConstructor;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
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
        for (Desire periodicDesire : periodicDesires) {

            // todo can we extract a predicate method/variable so that anymatch(matchesDateAndCourts)
            boolean matchesSameDaySameCourtsExplicit = explicitDesires.stream()
                    .filter(e -> e.getDate().equals(periodicDesire.getDate()))
                    .anyMatch(e -> new HashSet<>(e.getCourts()).containsAll(periodicDesire.getCourts()));

            if (!matchesSameDaySameCourtsExplicit && !isHoliday(periodicDesire.getDate())) {
                combined.add(periodicDesire);
            }
        }
        combined.addAll(explicitDesires);
        combined.sort(Comparator.comparing(Desire::getDate));
        return combined;
    }

    // todo find similar one in DesireOrderPairer (maybe even more places) and extract common one?
    private void validateDesires(List<Desire> desires) {
        Collection<List<Desire>> desiresByDay = desires.stream()
                .collect(Collectors.groupingBy(Desire::getDate))
                .values();
        for (List<Desire> daysDesires : desiresByDay) {
            // todo can I use reduce to count indoor and outdoor at the same time?
            List<Desire> in = new ArrayList<>();
            List<Desire> out = new ArrayList<>();
            for (Desire desire : daysDesires) {
                // todo this works as long as addNext(count, dayOfWeek, DESIRE draft) is PRIVATE, i.e. only indoor/outdoor is possible to request. Later, specific court-id-subset verification will be needed
                if (desire.getCourts().equals(Court.getIndoorIds())) in.add(desire);
                if (desire.getCourts().equals(Court.getOutdoorIds())) out.add(desire);
            }
            LocalDate date = daysDesires.get(0).getDate();
            if (in.size() > 1) throw new DuplicateDesiresException(date + " contains > 1 indoor desires");
            if (out.size() > 1) throw new DuplicateDesiresException(date + " contains > 1 outdoor desires");
        }
    }

    public DesireMaker addExplicitDesires() {
        this.explicitDesires = DesiresExplicit.makeExplicitDesires();
        return this;
    }

    public DesireMaker addNextInAndClay(int count, DayOfWeek dayOfWeek) {
        Desire indoorDesireDraft = new Desire(getNow(), Court.getIndoorIds());
        Desire clayDesireDraft = new Desire(getNow(), Court.getClayIds());
        addNext(count, dayOfWeek, indoorDesireDraft);
        addNext(count, dayOfWeek, clayDesireDraft);
        return this;
    }

    public DesireMaker addNextInAndOut(int count, DayOfWeek dayOfWeek) {
        Desire indoorDesireDraft = new Desire(getNow(), Court.getIndoorIds());
        Desire outdoorDesireDraft = new Desire(getNow(), Court.getOutdoorIds());
        addNext(count, dayOfWeek, indoorDesireDraft);
        addNext(count, dayOfWeek, outdoorDesireDraft);
        return this;
    }

    // todo make it season-specific. Summer -- default outdoors (+ maybe indoors). Winter -- indoors
    public DesireMaker addNext(int count, DayOfWeek dayOfWeek) {
        Desire regularDesireDraft = new Desire(getNow(), Court.getIndoorIds());  // todo write a test  // fixme: (ctrl-f FOO1 for 2 identical comments): related
        return addNext(count, dayOfWeek, regularDesireDraft);
    }

    // if we make this public, DesireOrderPairer and possibly DesireMaker would need extra tests, because
    // currently maker can  add via addNext() and addNextInAndOut() which limit periodic desires to
    //   - one-per-day
    //   - 2 per day -- exactly 1 indoor (all indoor courts) and 1 outdoor (all outdoor courts)
    // if we allow arbitrary court sets, extra tests and handling is needed.
    // e.g.
    //   - 2 specific courts in the shade outdoors + all indoors
    //   - 2 in shade + all outdoors (there's overlap, should fail, or handle in priority order)
    private DesireMaker addNext(int count, DayOfWeek dayOfWeek, Desire draft) {
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
