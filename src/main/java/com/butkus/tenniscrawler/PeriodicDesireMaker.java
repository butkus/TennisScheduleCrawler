package com.butkus.tenniscrawler;

import org.springframework.lang.NonNull;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

public class PeriodicDesireMaker {

    Integer count;
    DayOfWeek dayOfWeek;
    MyDesireBuilder draft;

    private final Clock clock;

    List<Desire> result = new ArrayList<>();
    LocalDate baseDate;

    public PeriodicDesireMaker(Integer count, DayOfWeek dayOfWeek, MyDesireBuilder draft, Clock clock) {
        this.count = count;
        this.dayOfWeek = dayOfWeek;
        this.draft = draft;
        this.clock = clock;

        this.baseDate = getNow();
    }

    @NonNull
    public List<Desire> make() {
        handleFirstDay();
        handleOtherDays();
        return result;
    }

    private void handleFirstDay() {
        if (baseDate.getDayOfWeek().equals(dayOfWeek)) {
            result.add(finalizeDraftAndBuild(draft, baseDate));
        } else {
            addNextDay();
        }
    }

    private void handleOtherDays() {
        // i = 1 because first day was handled separately
        for (int i = 1; i < count; i++) {
            addNextDay();
        }
    }

    private void addNextDay() {
        LocalDate next = baseDate.with(TemporalAdjusters.next(dayOfWeek));
        result.add(finalizeDraftAndBuild(draft, next));
        baseDate = next;
    }

    private LocalDate getNow() {
        return LocalDate.now(clock);
    }

    private static Desire finalizeDraftAndBuild(MyDesireBuilder myDesireBuilder, LocalDate date) {
        return myDesireBuilder.date(date).build();
    }

}
