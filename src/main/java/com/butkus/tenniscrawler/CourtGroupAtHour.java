package com.butkus.tenniscrawler;

import java.time.LocalTime;

public record CourtGroupAtHour(CourtGroup courtType, LocalTime time) {

}
