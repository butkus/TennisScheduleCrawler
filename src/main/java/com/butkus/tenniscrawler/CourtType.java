package com.butkus.tenniscrawler;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum CourtType {
    HARD (2, "Kieta danga"),
    CARPET (8, "Kilimas"),
    GRASS (20, "Žolė"),
    CLAY (5, "Gruntas"),
    HARD_2(18, "Plėtra");

    private final int courtTypeId;
    private final String translation;

    public static CourtType fromCourtId(Integer courtId) {
        for (CourtType court : CourtType.values()) {
            if (court.getCourtTypeId() == courtId) return court;
        }
        throw new RuntimeException("fromCourtId(): non-existent court requested");

    }

    public static List<Integer> getIds() {
        return Arrays.stream(CourtType.values())
                .map(CourtType::getCourtTypeId)
                .collect(Collectors.toList());
    }
}
