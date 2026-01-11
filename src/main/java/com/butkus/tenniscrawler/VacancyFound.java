package com.butkus.tenniscrawler;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@RequiredArgsConstructor
@Data
public class VacancyFound {
    private final Long courtId;
    private final LocalDate date;
    private final LocalTime from;
    private final LocalTime to;

    @Override
    public String toString() {
        return "Found court " + courtId + ": " + date + ", " + from + "-" + to;
    }
}
