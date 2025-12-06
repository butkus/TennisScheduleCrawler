package com.butkus.tenniscrawler;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalTime;

@Data
@RequiredArgsConstructor
public class CourtTypeAtHour {

    private final CourtTypeCustom courtType;
    private final LocalTime time;

}
