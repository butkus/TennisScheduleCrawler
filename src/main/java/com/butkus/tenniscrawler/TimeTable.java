package com.butkus.tenniscrawler;

import org.openqa.selenium.WebElement;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class TimeTable {

    private static final String GREY_CLASS = "ex_bus";    // grey   -- unavailable
    private static final String YELLOW_CLASS = "ex_sal";  // yellow -- for sale, not reserve
    private static final String ORANGE_CLASS = "ex_car";  // orange -- YOUR reserved (and paid for) time
    private static final String GREEN_CLASS = "ex_res";   // green  -- slot in cart

    private static final Integer WHITE = 1;
    private static final Integer GREY = 2;
    private static final Integer YELLOW = 3;
    private static final Integer ORANGE = 4;
    private static final Integer GREEN = 5;

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
    private String courtName;
    private List<Integer> aggregatedCourts;

    public TimeTable(List<WebElement> webElements, String date, int placeId) {
        this.slots = resolveSlots(webElements);
        getAggregatedSlotsForTheDay();
        this.date = LocalDate.parse(date);
        if (placeId == 2) {
            this.courtName = "Kieta danga";
        } else if (placeId == 8) {
            this.courtName = "Kilimas";
        }
    }

    public boolean isOfferFound() {
        boolean hasReserved = slots.stream().anyMatch(e -> e.getClasses().contains(ORANGE_CLASS));      // todo replace with check in aggregated slots

        if (!hasReserved) {
            for (int i=0; i<5; i++) {
                // at least 2 adjacent white slots
                if (aggregatedCourts.get(i).equals(WHITE) && aggregatedCourts.get(i + 1).equals(WHITE)) {
                    return true;
                }
            }
        } else {
            for (int i=1; i<5; i++) {
                if (aggregatedCourts.get(i).equals(ORANGE)) {
                    if (aggregatedCourts.get(i-1).equals(WHITE) || aggregatedCourts.get(i+1).equals(WHITE)) {
                        return true;    // can extend/move booked (orange) time
                    }
                }
            }
        }
        return false;
    }

    private void getAggregatedSlotsForTheDay() {
        List<String> courts = slots.stream()
                .map(Slot::getCourt)
                .distinct()
                .collect(toList());

        List<Integer> aggregatedCourts = new ArrayList<>(Arrays.asList(0, 0, 0, 0, 0, 0));

        for (String court : courts) {
            List<Slot> courtSlots = slots.stream()
                    .filter(e -> court.equals(e.getCourt()))
                    .collect(toList());
            List<Integer> aggregatedCourt = aggregateCourt(courtSlots);
            coalesceCourts(aggregatedCourts, aggregatedCourt);
        }
        this.aggregatedCourts = aggregatedCourts;       // todo unify naming
    }

    public String getReadableAggregatedCourt() {
        if (this.aggregatedCourts == null || this.aggregatedCourts.isEmpty())
            throw new RuntimeException("Can't get readable court info from empty court");
        String times = aggregatedCourts.toString();
        String niceDate = date + ", " + date.getDayOfWeek();
        return String.format("%-21s %s  %9s", niceDate, times, courtName);       // fixme: replace arbitrary 21 and 9
    }

    private static List<Integer> aggregateCourt(List<Slot> courtSlots) {
        List<Slot> acceptableSlots = courtSlots.stream().filter(e -> ACCEPTABLE_TIMES.contains(e.getTime())).collect(toList());

        Integer[] aggregatedSlots = new Integer[]{0, 0, 0, 0, 0, 0};

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

        if (slotFree1800) aggregatedSlots[0] = WHITE;
        if (slotOrange1800) aggregatedSlots[0] = ORANGE;
        if (slotFree1830 || (slotFree1800 && slotDoesNotExist(acceptableSlots, S18_30))){
            aggregatedSlots[1] = WHITE;
        }
        if (slotOrange1830 || (slotOrange1800 && slotDoesNotExist(acceptableSlots, S18_30))){
            aggregatedSlots[1] = ORANGE;
        }


        if (slotFree1900) aggregatedSlots[2] = WHITE;
        if (slotOrange1900) aggregatedSlots[2] = ORANGE;
        if (slotFree1930 || (slotFree1900 && slotDoesNotExist(acceptableSlots, S19_30))){
            aggregatedSlots[3] = WHITE;
        }
        if (slotOrange1930 || (slotOrange1900 && slotDoesNotExist(acceptableSlots, S19_30))){
            aggregatedSlots[3] = ORANGE;
        }


        if (slotFree2000) aggregatedSlots[4] = WHITE;
        if (slotOrange2000) aggregatedSlots[4] = ORANGE;
        if (slotFree2030 || (slotFree2000 && slotDoesNotExist(acceptableSlots, S20_30))){
            aggregatedSlots[5] = WHITE;
        }
        if (slotOrange2030 || (slotOrange2000 && slotDoesNotExist(acceptableSlots, S20_30))){
            aggregatedSlots[5] = ORANGE;
        }

        return new ArrayList<>(Arrays.asList(aggregatedSlots));
    }

    private static void coalesceCourts(List<Integer> aggregatedCourts, List<Integer> newCourt) {
        for (int i=0; i<6; i++) {
            if (newCourt.get(i).equals(ORANGE)) {
                aggregatedCourts.set(i, ORANGE);
            } else if (newCourt.get(i).equals(WHITE)) {
                aggregatedCourts.set(i, WHITE);
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
}
