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
    private final ExtensionInterest extensionInterest;

    private final boolean hasReservationInCurrentCourt;
    private final List<Integer> otherCourt;
    private final boolean hasReservationInOtherCourt;

    public SlotFinder(Cache cache, List<Integer> currentCourt, LocalDate date, Integer courtId, ExtensionInterest extensionInterest) {
        this.currentCourt = currentCourt;
        this.date = date;
        this.courtId = courtId;
        this.extensionInterest = extensionInterest;

        this.hasReservationInCurrentCourt = currentCourt.stream().anyMatch(ORANGE::equals);
        Integer otherCourtId = courtId == HARD ? CARPET : HARD;
        this.otherCourt = cache.get(Pair.with(date, otherCourtId));
        this.hasReservationInOtherCourt = otherCourt != null;
    }

    public boolean isOfferFound() {
        if (extensionInterest == NONE) return false;

        if (hasReservationInCurrentCourt) {
           if (extensionInterest == EARLIER) {
                return found1EarlierAdjacentSlot() || found2EarlierSlotsThanReservedIn(currentCourt);
            } else if (extensionInterest == LATER) {
                return found1LaterAdjacentSlot() || found2LaterSlotsThanReservedIn(currentCourt);
            } else if (extensionInterest == ANY) {
               return found1SlotOnEitherSide();
           }
        } else {
            // does NOT have reservation in current court
            if (extensionInterest == EARLIER) {
                if (hasReservationInOtherCourt) {
                    return found2EarlierSlotsThanReservedIn(otherCourt);
                } else {
                    logRequestedExtensionButNoBookingFound();
                    return false;
                }
            } else if (extensionInterest == LATER) {
                if (hasReservationInOtherCourt) {
                    return found2LaterSlotsThanReservedIn(otherCourt);
                } else {
                    logRequestedExtensionButNoBookingFound();
                    return false;
                }
            } else if (extensionInterest == ANY) {
                if (hasReservationInOtherCourt) {
                    return found2EarlierSlotsThanReservedIn(otherCourt) || found2LaterSlotsThanReservedIn(otherCourt);
                } else {
                    return found2FreeSlots();
                }
            }
        }
        return false;
    }

    private boolean found2FreeSlots() {
        for (int i=0; i<5; i++) {
            if (currentCourt.get(i).equals(WHITE) && currentCourt.get(i + 1).equals(WHITE)) {
                return true;
            }
        }
        return false;
    }

    private boolean found1SlotOnEitherSide() {
        for (int i=1; i<5; i++) {
            if (currentCourt.get(i).equals(ORANGE) && (currentCourt.get(i-1).equals(WHITE) || currentCourt.get(i+1).equals(WHITE))) {
                return true;
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

    private Integer getFirstBookedSlotPosition(List<Integer> aggregatedCourt) {
        Integer firstBookedSlotPos = null;
        for (int i = 0; i<aggregatedCourt.size(); i++) {
            if (aggregatedCourt.get(i).equals(ORANGE)){
                firstBookedSlotPos = i;
                break;
            }
        }
        return  firstBookedSlotPos;
    }

    private Integer getLastBookedSlotPosition(List<Integer> aggregatedCourt) {
        Integer lastBookedSlotPos = null;
        for (int i=aggregatedCourt.size()-1; i>0; i--) {
            if (aggregatedCourt.get(i).equals(ORANGE)){
                lastBookedSlotPos = i;
                break;
            }
        }
        return  lastBookedSlotPos;
    }

    private void logRequestedExtensionButNoBookingFound() {
        String courtNameEng = courtId.equals(2) ? "HARD" : "CARPET";
        System.out.printf("Requested %s for  %s in %s court  but no existing booking%n", extensionInterest, date, courtNameEng);
    }

}
