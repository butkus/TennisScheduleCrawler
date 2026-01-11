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
    HARD_2(18, "Plėtra"),

    SQUASH(4, "Skvošas"),
    BADMINTON(9, "Badmintonas"),
    MINI_TENNIS(15, "MINI tenisas"),
    BERNARDINAI_PADEL(21, "Bernardinai padelio kortai");


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

    public static List<Integer> getIdsForPlaceFetcher() {
//        - {"excludeCourtName":true,"excludeInfoUrl":true,"places":[2,18,5,20,8],"dates":["2026-01-01","2026-01-02","2026-01-03","2026-01-04","2026-01-05","2026-01-06","2026-01-07","2026-01-08"],"salePoint":11,"sessionToken":"93b25e67d079e4871686c18b02fe62f9"}
        return List.of(HARD.courtTypeId, HARD_2.courtTypeId, CLAY.courtTypeId, GRASS.courtTypeId, CARPET.courtTypeId);
    }
}
