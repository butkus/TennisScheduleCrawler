package com.butkus.tenniscrawler;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.openqa.selenium.WebElement;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.butkus.tenniscrawler.Colors.ORANGE;
import static com.butkus.tenniscrawler.Colors.WHITE;
import static com.butkus.tenniscrawler.ExtensionInterest.NONE;
import static java.util.stream.Collectors.toList;

public class TimeTable {

    private static final String GREY_CLASS = "ex_bus";    // grey   -- unavailable
    private static final String YELLOW_CLASS = "ex_sal";  // yellow -- for sale, not reserve
    private static final String ORANGE_CLASS = "ex_car";  // orange -- YOUR reserved (and paid for) time
    private static final String GREEN_CLASS = "ex_res";   // green  -- slot in cart

    public static final String CLASS = "class";
    public static final String COURT = "data-court";
    public static final String TIME = "data-time";

    public static final String S18_00 = "18:00";
    public static final String S18_30 = "18:30";
    public static final String S19_00 = "19:00";
    public static final String S19_30 = "19:30";
    public static final String S20_00 = "20:00";
    public static final String S20_30 = "20:30";
    protected static final List<String> ACCEPTABLE_TIMES = Arrays.asList(S18_00, S18_30, S19_00, S19_30, S20_00, S20_30);

    private final List<Slot> slots;
    private final LocalDate date;
    private final Court court;
    private final ExtensionInterest extensionInterest;

    @Getter
    private final List<Integer> aggregatedCourt;

    public TimeTable(List<WebElement> webElements, Triplet<LocalDate, Integer, ExtensionInterest> dayAtCourt) {
        this.slots = resolveSlots(webElements);
        this.aggregatedCourt = getAggregatedCourtForTheDay();
        this.date = dayAtCourt.getValue0();
        this.court = Court.fromCourtId(dayAtCourt.getValue1());
        this.extensionInterest = dayAtCourt.getValue2();
    }

    public boolean isOfferFound(Cache cache) {
        SlotFinder slotFinder = new SlotFinder(cache, aggregatedCourt, date, court, extensionInterest);
        return slotFinder.isOfferFound();
    }

    private List<Integer> getAggregatedCourtForTheDay() {
        List<String> courtNumbers = slots.stream()
                .map(Slot::getCourtNo)
                .distinct()
                .collect(toList());

        List<Integer> aggregatedCourtsForTheDay = new ArrayList<>(Arrays.asList(0, 0, 0, 0, 0, 0));

        for (String courtNo : courtNumbers) {
            List<Slot> courtSlots = slots.stream()
                    .filter(e -> courtNo.equals(e.getCourtNo()))
                    .collect(toList());
            List<Integer> courtSchedule = resolveCourt(courtSlots);
            aggregateCourts(aggregatedCourtsForTheDay, courtSchedule);
        }
        return aggregatedCourtsForTheDay;
    }

    private static List<Integer> resolveCourt(List<Slot> courtSlots) {
        List<Slot> acceptableSlots = courtSlots.stream().filter(e -> ACCEPTABLE_TIMES.contains(e.getTime())).collect(toList());

        Integer[] court = new Integer[]{0, 0, 0, 0, 0, 0};

        boolean slotFree1800 = isSlotFree(acceptableSlots, S18_00);
        boolean slotFree1830 = isSlotFree(acceptableSlots, S18_30);
        boolean slotFree1900 = isSlotFree(acceptableSlots, S19_00);
        boolean slotFree1930 = isSlotFree(acceptableSlots, S19_30);
        boolean slotFree2000 = isSlotFree(acceptableSlots, S20_00);
        boolean slotFree2030 = isSlotFree(acceptableSlots, S20_30);

        boolean slotOrange1800 = isSlotOrange(acceptableSlots, S18_00);
        boolean slotOrange1830 = isSlotOrange(acceptableSlots, S18_30);
        boolean slotOrange1900 = isSlotOrange(acceptableSlots, S19_00);
        boolean slotOrange1930 = isSlotOrange(acceptableSlots, S19_30);
        boolean slotOrange2000 = isSlotOrange(acceptableSlots, S20_00);
        boolean slotOrange2030 = isSlotOrange(acceptableSlots, S20_30);

        if (slotFree1800) court[0] = WHITE;
        if (slotOrange1800) court[0] = ORANGE;
        if (slotFree1830 || (slotFree1800 && slotDoesNotExist(acceptableSlots, S18_30))){
            court[1] = WHITE;
        }
        if (slotOrange1830 || (slotOrange1800 && slotDoesNotExist(acceptableSlots, S18_30))){
            court[1] = ORANGE;
        }


        if (slotFree1900) court[2] = WHITE;
        if (slotOrange1900) court[2] = ORANGE;
        if (slotFree1930 || (slotFree1900 && slotDoesNotExist(acceptableSlots, S19_30))){
            court[3] = WHITE;
        }
        if (slotOrange1930 || (slotOrange1900 && slotDoesNotExist(acceptableSlots, S19_30))){
            court[3] = ORANGE;
        }


        if (slotFree2000) court[4] = WHITE;
        if (slotOrange2000) court[4] = ORANGE;
        if (slotFree2030 || (slotFree2000 && slotDoesNotExist(acceptableSlots, S20_30))){
            court[5] = WHITE;
        }
        if (slotOrange2030 || (slotOrange2000 && slotDoesNotExist(acceptableSlots, S20_30))){
            court[5] = ORANGE;
        }

        return new ArrayList<>(Arrays.asList(court));
    }

    private static void aggregateCourts(List<Integer> aggregatedCourt, List<Integer> newCourt) {
        for (int i=0; i<6; i++) {
            if (newCourt.get(i).equals(ORANGE)) {
                aggregatedCourt.set(i, ORANGE);
            } else if (newCourt.get(i).equals(WHITE) && !aggregatedCourt.get(i).equals(ORANGE)) {
                aggregatedCourt.set(i, WHITE);
            }
        }
    }

    private static boolean slotDoesNotExist(List<Slot> slots, String slotTime) {
        return slots.stream().noneMatch(e -> slotTime.equals(e.getTime()));
    }

    private static boolean isSlotFree(List<Slot> slots, String time) {
        return slots.stream()
                .filter(e -> time.equals(e.getTime()))
                .anyMatch(TimeTable::isCourtAvailableAtTime);
    }

    private static boolean isSlotOrange(List<Slot> slots, String time) {
        return slots.stream()
                .filter(e -> time.equals(e.getTime()))
                .anyMatch(TimeTable::isCourtOrangeAtTime);
    }

    private static boolean isCourtAvailableAtTime(Slot e) {
        String classes = e.getClasses();
        return !classes.contains(GREY_CLASS) &&
                !classes.contains(GREEN_CLASS) &&
                !classes.contains(YELLOW_CLASS) &&
                !classes.contains(ORANGE_CLASS);
    }

    private static boolean isCourtOrangeAtTime(Slot e) {
        return e.getClasses().contains(ORANGE_CLASS);
    }

    private List<Slot> resolveSlots(List<WebElement> webElements) {
        return webElements.stream()
                .map(this::resolveSlot)
                .collect(Collectors.toList());
    }

    private Slot resolveSlot(WebElement webElement) {
        String classes = webElement.getAttribute(CLASS);
        String court = webElement.getAttribute(COURT);
        String time = webElement.getAttribute(TIME);
        return new Slot(classes, court, time);
    }

    public void updateFromCache(Cache cache) {
        List<Integer> cached = cache.get(Pair.with(date, court.getCourtId()));
        if (cached != null) {
            aggregateCourts(this.aggregatedCourt, cached);
        }
    }

    public void printTable(Cache cache) {
        String niceDate = date + ", " + Translations.getLtWeekDay(date);
        String courtMap = aggregatedCourt.toString();

        String extensionInterestSign = getExtensionInterestSign();

        String courtNameCentered = getCenteredCourtName();
        String foundNotFoundMark = isOfferFound(cache) ? "‹✔›" : "\uD83D\uDFA8";   // IntelliJ UTF-8 console output issue: https://stackoverflow.com/a/56430344
        System.out.printf("%-20s %s  %s  %s  %5s%n", niceDate, courtMap, extensionInterestSign, courtNameCentered, foundNotFoundMark);
    }

    private String getExtensionInterestSign() {
        boolean hasBookedSlots = aggregatedCourt.stream().anyMatch(e -> e.equals(ORANGE));
        if (extensionInterest == NONE) {
            return extensionInterest.getSign();
        } else if (hasBookedSlots) {
            return extensionInterest.getSign();
        } else {
            return " ";
        }
    }

    private String getCenteredCourtName() {
        String courtName = court.getTranslation();

        int longestCourtName = 0;
        for (Court c : Court.values()) {
            longestCourtName = Math.max(longestCourtName, c.getTranslation().length());
        }
        return StringUtils.center(courtName, longestCourtName);
    }

}
