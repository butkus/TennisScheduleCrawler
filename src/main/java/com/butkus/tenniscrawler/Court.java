package com.butkus.tenniscrawler;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum Court {

    // TENNIS
    H01(1L, "SEB 01", CourtType.HARD),
    H02(7L, "SEB 02", CourtType.HARD),
    H03(8L, "SEB 03", CourtType.HARD),
    H04(10L, "SEB 04", CourtType.HARD),
    H05(11L, "SEB 05", CourtType.HARD),
    H06(12L, "SEB 06", CourtType.HARD),
    H07(13L, "SEB 07", CourtType.HARD),
    H08(14L, "SEB 08", CourtType.HARD),
    H09(15L, "SEB 09", CourtType.HARD),
    H10(16L, "SEB 10", CourtType.HARD),
    H11(17L, "SEB 11", CourtType.HARD),
    H12(18L, "SEB 12", CourtType.HARD),
    H13(19L, "SEB 13", CourtType.HARD),
    H14(20L, "SEB 14", CourtType.HARD),
    H15(21L, "SEB 15", CourtType.HARD),

    H16(135L, "SEB 16", CourtType.HARD_2),
    H17(137L, "SEB 17", CourtType.HARD_2),
    H18(139L, "SEB 18", CourtType.HARD_2),
    H19(141L, "SEB 19", CourtType.HARD_2),
    H20(143L, "SEB 20", CourtType.HARD_2),
    H21(145L, "SEB 21", CourtType.HARD_2),
    HCC(147L, "Centrinis (CC)", CourtType.HARD_2),

    K1(59L, "K1", CourtType.CARPET),
    K2(61L, "K2", CourtType.CARPET),
    K3(63L, "K3", CourtType.CARPET),
    K4(65L, "K4", CourtType.CARPET),
    K5(67L, "K5", CourtType.CARPET),
    K6(69L, "K6", CourtType.CARPET),

    C01(44L, "BS 01 gruntas", CourtType.CLAY),
    C02(45L, "BS 02 gruntas", CourtType.CLAY),
    C03(46L, "BS 03 gruntas", CourtType.CLAY),
    C04(47L, "BS 04 gruntas", CourtType.CLAY),
    C05(48L, "BS 05 gruntas", CourtType.CLAY),
    C06(49L, "BS 06 gruntas", CourtType.CLAY),
    C07(50L, "BS 07 gruntas", CourtType.CLAY),
    C08(51L, "BS 08 gruntas", CourtType.CLAY),
    C09(52L, "BS 09 gruntas", CourtType.CLAY),
    C10(53L, "BS 10 gruntas", CourtType.CLAY),

    G1(54L, "BS 11 sint. žolė", CourtType.GRASS),
    G2(55L, "BS 12 sint. žolė", CourtType.GRASS),


    // SQUASH
    S1(34L, "*Skvošas Nr.1", CourtType.SQUASH),     // ORIGINAL NAME: Skvošas Nr.1
    S2(35L, "*Skvošas Nr.2", CourtType.SQUASH),
    S3(36L, "*Skvošas Nr.3", CourtType.SQUASH),
    S4(37L, "*Skvošas Nr.4", CourtType.SQUASH);


    private final Long courtId;
    private final String courtName;
    private final CourtType courtType;

    public static List<Long> getIds() {
        return getIds(e -> true);
    }

    public static List<Long> getSquashIds() {
        return getIds(e -> e.courtType == CourtType.SQUASH);
    }

    public static List<Long> getNonSquashIds() {
        return getIds(e -> e.courtType != CourtType.SQUASH);
    }

    public static List<Long> getIndoorIds() {
        return getIds(e -> e.courtType == CourtType.HARD || e.courtType == CourtType.HARD_2 || e.courtType == CourtType.CARPET);
    }

    public static List<Long> getOutdoorIds() {
        return getIds(e -> e.courtType == CourtType.CLAY || e.courtType == CourtType.GRASS);
    }

    public static List<Long> getHardIds() {
        return getIds(e -> e.courtType == CourtType.HARD || e.courtType == CourtType.HARD_2);
    }

    public static List<Long> getCarpetIds() {
        return getIds(e -> e.courtType == CourtType.CARPET);
    }

    public static List<Long> getClayIds() {
        return getIds(e -> e.courtType == CourtType.CLAY);
    }

    public static List<Long> getGrassIds() {
        return getIds(e -> e.courtType == CourtType.GRASS);
    }

    public static List<Long> getIds(Predicate<Court> courtPredicate) {
        return Arrays.stream(values())
                .filter(courtPredicate)
                .map(Court::getCourtId)
                .collect(Collectors.toList());
    }

    public static Court getByName(String name) {
        for (Court value : values()) {
            if (value.courtName.equals(name)) return value;
        }
        throw new RuntimeException(String.format("Court by name %s not found", name));
    }

    public static Court getByCourtId(long id) {
        for (Court value : values()) {
            if (value.courtId == id) return value;
        }
        throw new RuntimeException(String.format("Court by courtId %s not found", id));
    }

    public static List<Long> getByCourtTypes(List<Integer> courtTypes) {
        return Arrays.stream(values())
                .filter(e -> courtTypes.contains(e.getCourtType().getCourtTypeId()))
                .map(Court::getCourtId)
                .collect(Collectors.toList());
    }
}
