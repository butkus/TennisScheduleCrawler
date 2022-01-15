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

    private final List<Integer> currentCourt;
    private final LocalDate date;
    private final Integer courtId;
    private final String courtName;
    private final ExtensionInterest extensionInterest;

    private final boolean noReservationInCurrentCourt;
    private final List<Integer> otherCourt;

    public SlotFinder(Cache cache, List<Integer> currentCourt, LocalDate date, Integer courtId, String courtName, ExtensionInterest extensionInterest) {
        this.currentCourt = currentCourt;
        this.date = date;
        this.courtId = courtId;
        this.courtName = courtName;
        this.extensionInterest = extensionInterest;

        this.noReservationInCurrentCourt = currentCourt.stream().noneMatch(ORANGE::equals);
        Integer otherCourtId = courtId == HARD ? CARPET : HARD;
        this.otherCourt = cache.get(Pair.with(date, otherCourtId));
    }

    public boolean isOfferFound() {
        if (extensionInterest == NONE) return false;

        if (noReservationInCurrentCourt) {

            if (extensionInterest == EARLIER) {
                if (otherCourt == null) {
                    System.out.printf("Requested %s for date=%s and court=%s (courtId=%s) but no existing booking%n", extensionInterest, date, courtName, courtId);
                    return false;
                } else {
                    if (found2EarlierSlotsThanReservedIn(otherCourt)) return true;
                }
            } else if (extensionInterest == LATER) {
                if (otherCourt == null) {
                    System.out.printf("Requested %s for date=%s and court=%s (courtId=%s) but no existing booking%n", extensionInterest, date, courtName, courtId);
                    return false;
                } else {
                    if (found2LaterSlotsThanReservedIn(otherCourt)) return true;
                }
            } else if (extensionInterest == ANY) {
                if (otherCourt == null) {
                    // not booked -- find at least 2 adjacent free slots
                    for (int i=0; i<5; i++) {
                        if (currentCourt.get(i).equals(WHITE) && currentCourt.get(i + 1).equals(WHITE)) {
                            return true;
                        }
                    }
                } else {
                    // has booking in other court type -- find 2 adjacent free slots before or after it
                    if (found2EarlierSlotsThanReservedIn(otherCourt) || found2LaterSlotsThanReservedIn(otherCourt)) return true;
                }
            }
        } else {
            // we DO have reserved for current court type
            if (extensionInterest == ANY) {
                for (int i=1; i<5; i++) {
                    if (currentCourt.get(i).equals(ORANGE) && (currentCourt.get(i-1).equals(WHITE) || currentCourt.get(i+1).equals(WHITE))) {
                        return true;
                    }
                }
            } else if (extensionInterest == EARLIER) {
                if (found1EarlierAdjacentSlot() || found2EarlierSlotsThanReservedIn(currentCourt)) return true;
            } else if (extensionInterest == LATER) {
                if (found1LaterAdjacentSlot() || found2LaterSlotsThanReservedIn(currentCourt)) return true;
            }

        }
        return false;
    }

    private boolean found1EarlierAdjacentSlot() {
        for (int i=1; i<6; i++) {
            if (currentCourt.get(i).equals(ORANGE) && currentCourt.get(i-1).equals(WHITE)) {
                return true;
            }
        }
        return false;
    }

    private boolean found1LaterAdjacentSlot() {
        for (int i=0; i<5; i++) {
            if (currentCourt.get(i).equals(ORANGE) && currentCourt.get(i+1).equals(WHITE)) {
                return true;
            }
        }
        return false;
    }

    private boolean found2EarlierSlotsThanReservedIn(List<Integer> reservedCourt) {
        // find 2 adjacent slots and first slot is earlier than first reservedCourt ORANGE slot
        int firstBookedSlotPos = getFirstBookedSlotPosition(reservedCourt);
        for (int i = 0; i < 5; i++) {
            if (currentCourt.get(i).equals(WHITE) && currentCourt.get(i + 1).equals(WHITE) && i < firstBookedSlotPos) {
                return true;
            }
        }
        return false;
    }

    private boolean found2LaterSlotsThanReservedIn(List<Integer> reservedCourt) {
        // find 2 adjacent slots with latter slot being later than last reservedCourt ORANGE slot
        int lastBookedSlotPos = getLastBookedSlotPosition(reservedCourt);
        for (int i = 0; i < 5; i++) {
            if (currentCourt.get(i).equals(WHITE) && currentCourt.get(i + 1).equals(WHITE) && i + 1 > lastBookedSlotPos) {
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
