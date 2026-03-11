package com.butkus.tenniscrawler;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalTime;

@Data
@RequiredArgsConstructor
public class CourtGroupAtHour {

    private final CourtGroup courtType;
    private final LocalTime time;

}
