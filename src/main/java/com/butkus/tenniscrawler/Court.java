package com.butkus.tenniscrawler;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Court {
    HARD (2, "Kieta danga"),
    CARPET (8, "Kilimas"),
    GRASS (20, "Žolė"),
    CLAY (5, "Gruntas"),
    HARD_2(18, "Plėtra");

    private final int courtId;
    private final String translation;

    public static Court fromCourtId(Integer courtId) {
        for (Court court : Court.values()) {
            if (court.getCourtId() == courtId) return court;
        }
        throw new RuntimeException("fromCourtId(): non-existent court requested");

    }
}
