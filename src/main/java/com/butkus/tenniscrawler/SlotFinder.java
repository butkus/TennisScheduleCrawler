package com.butkus.tenniscrawler;

import org.javatuples.Pair;

import java.time.LocalDate;
import java.util.List;

import static com.butkus.tenniscrawler.Colors.ORANGE;
import static com.butkus.tenniscrawler.Colors.WHITE;
import static com.butkus.tenniscrawler.Court.CARPET;
import static com.butkus.tenniscrawler.Court.HARD;
import static com.butkus.tenniscrawler.ExtensionInterest.*;

public class SlotFinder {

    private final List<Integer> aggregatedCourts;
    private final LocalDate date;
    private final Integer courtId;
    private final String courtName;
    private final ExtensionInterest extensionInterest;

    private final boolean doesNotHaveReservationForCurrentCourtType;
    private final List<Integer> aggregatedCourtsOtherType;

    public SlotFinder(Cache cache, List<Integer> aggregatedCourts, LocalDate date, Integer courtId, String courtName, ExtensionInterest extensionInterest) {
        this.aggregatedCourts = aggregatedCourts;
        this.date = date;
        this.courtId = courtId;
        this.courtName = courtName;
        this.extensionInterest = extensionInterest;

        this.doesNotHaveReservationForCurrentCourtType = aggregatedCourts.stream().noneMatch(ORANGE::equals);
        Integer otherCourtId = courtId == HARD ? CARPET : HARD;
        this.aggregatedCourtsOtherType = cache.get(Pair.with(date, otherCourtId));
    }

    public boolean isOfferFound() {
        if (extensionInterest == NONE) return false;

        if (doesNotHaveReservationForCurrentCourtType) {

            if (extensionInterest == EARLIER) {
                if (aggregatedCourtsOtherType == null) {        // todo use boolean hasReservationInOtherCourtType
                    // nothing booked in other court type
                    System.out.printf("Requested %s for date=%s and court=%s (courtId=%s) but no existing booking%n", extensionInterest, date, courtName, courtId);
                    return false;   // lets log and carry on for now
                } else {
                    if (find2EarlierSlotsThanReserved()) return true;
                }
            } else if (extensionInterest == LATER) {
                if (aggregatedCourtsOtherType == null) {
                    // nothing booked in other court type
                    System.out.printf("Requested %s for date=%s and court=%s (courtId=%s) but no existing booking%n", extensionInterest, date, courtName, courtId);
                    return false;   // lets log and carry on for now
                } else {
                    if (find2LaterSlotsThanReserved()) return true;
                }
            } else if (extensionInterest == BOTH) {     // todo rename BOTH ot EITHER? makes more sense here
                if (aggregatedCourtsOtherType == null) {
                    // not booked -- find at least 2 adjacent free slots (=== we do treat BOTH as -any- in this case ===)
                    for (int i=0; i<5; i++) {
                        if (aggregatedCourts.get(i).equals(WHITE) && aggregatedCourts.get(i + 1).equals(WHITE)) {
                            return true;
                        }
                    }
                } else {
                    // has booking in other court type -- find 2 adjacent free slots before or after it
                    if (find2EarlierSlotsThanReserved()) return true;
                    if (find2LaterSlotsThanReserved()) return true;
                }
            }
        } else {
            // we DO have reserved for current court type
            if (extensionInterest == BOTH) {
                for (int i=1; i<5; i++) {
                    if (aggregatedCourts.get(i).equals(ORANGE) && (aggregatedCourts.get(i-1).equals(WHITE) || aggregatedCourts.get(i+1).equals(WHITE))) {
                        return true;
                    }
                }
            } else if (extensionInterest == EARLIER) {
                for (int i=1; i<6; i++) {
                    if (aggregatedCourts.get(i).equals(ORANGE) && aggregatedCourts.get(i-1).equals(WHITE)) {
                        return true;
                    }
                }
            } else if (extensionInterest == LATER) {
                for (int i=0; i<5; i++) {
                    if (aggregatedCourts.get(i).equals(ORANGE) && aggregatedCourts.get(i+1).equals(WHITE)) {
                        return true;
                    }
                }
            }

        }
        return false;
    }

    private boolean find2LaterSlotsThanReserved() {
        // find 2 adjacent slots with latter slot being later than last aggregatedCourtsOtherType ORANGE slot
        int lastBookedSlotPos = getLastBookedSlotPosition(aggregatedCourtsOtherType);
        for (int i = 0; i < 5; i++) {
            if (aggregatedCourts.get(i).equals(WHITE) && aggregatedCourts.get(i + 1).equals(WHITE) && i + 1 > lastBookedSlotPos) {
                return true;
            }
        }
        return false;
    }

    private boolean find2EarlierSlotsThanReserved() {
        // find 2 adjacent slots and first slot is earlier than first aggregatedCourtsOtherType ORANGE slot
        int firstBookedSlotPos = getFirstBookedSlotPosition(aggregatedCourtsOtherType);
        for (int i = 0; i < 5; i++) {
            if (aggregatedCourts.get(i).equals(WHITE) && aggregatedCourts.get(i + 1).equals(WHITE) && i < firstBookedSlotPos) {
                return true;
            }
        }
        return false;
    }

    private Integer getFirstBookedSlotPosition(List<Integer> aggregatedCourt) {     // todo what if null?
        Integer firstBookedSlotPos = null;
        for (int i = 0; i<aggregatedCourt.size(); i++) {
            if (aggregatedCourt.get(i).equals(ORANGE)){
                firstBookedSlotPos = i;
                break;
            }
        }
        return  firstBookedSlotPos;
    }

    private Integer getLastBookedSlotPosition(List<Integer> aggregatedCourt) {     // todo what if null?
        Integer lastBookedSlotPos = null;
        for (int i=aggregatedCourt.size()-1; i>0; i--) {
            if (aggregatedCourt.get(i).equals(ORANGE)){
                lastBookedSlotPos = i;
                break;
            }
        }
        return  lastBookedSlotPos;
    }

}
