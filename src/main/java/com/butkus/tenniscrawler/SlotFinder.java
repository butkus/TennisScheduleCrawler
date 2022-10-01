package com.butkus.tenniscrawler;

import org.javatuples.Pair;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.butkus.tenniscrawler.Colors.*;
import static com.butkus.tenniscrawler.ExtensionInterest.*;

public class SlotFinder {

    private final List<Integer> currentCourt;
    private final LocalDate date;
    private final Integer courtId;
    private final ExtensionInterest extensionInterest;

    private final boolean hasReservationInCurrentCourt;
    private final List<List<Integer>> otherCourts = new ArrayList<>();
    private boolean hasReservationInOtherCourt;

    public SlotFinder(Cache cache, List<Integer> currentCourt, LocalDate date, Court court, ExtensionInterest extensionInterest) {
        this.currentCourt = currentCourt;
        this.date = date;
        this.courtId = court.getCourtId();
        this.extensionInterest = extensionInterest;

        this.hasReservationInCurrentCourt = currentCourt.stream().anyMatch(ORANGE::equals);

        for (Court ct : Court.values()) {
            boolean notCurrentCourt = ct.getCourtId() != court.getCourtId();
            List<Integer> courtSchedule = cache.get(Pair.with(date, ct.getCourtId()));
            if (notCurrentCourt && courtSchedule != null) {
                this.hasReservationInOtherCourt = true;
                this.otherCourts.add(courtSchedule);
            }

        }
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
                    return found2EarlierSlotsThanReservedInOtherCourts();
                } else {
                    logRequestedExtensionButNoBookingFound();
                    return false;
                }
            } else if (extensionInterest == LATER) {
                if (hasReservationInOtherCourt) {
                    return found2LaterSlotsThanReservedInOtherCourts();
                } else {
                    logRequestedExtensionButNoBookingFound();
                    return false;
                }
            } else if (extensionInterest == ANY) {
                if (hasReservationInOtherCourt) {
                    return found2EarlierSlotsThanReservedInOtherCourts() || found2LaterSlotsThanReservedInOtherCourts();
                } else {
                    return found2FreeSlots();
                }
            }
        }
        return false;
    }

    private boolean found2FreeSlots() {
        for (int i=0; i<5; i++) {
            boolean foundWhite = currentCourt.get(i).equals(WHITE) && currentCourt.get(i + 1).equals(WHITE);
            boolean foundYellow = currentCourt.get(i).equals(YELLOW) && currentCourt.get(i + 1).equals(YELLOW);
            if (foundWhite || foundYellow) {
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

    private boolean found2EarlierSlotsThanReservedInOtherCourts() {
        boolean found = false;
        for (List<Integer> otherCourt : otherCourts) {
            if (found2EarlierSlotsThanReservedIn(otherCourt)) {
                found = true;
                break;
            }
        }
        return found;
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

    private boolean found2LaterSlotsThanReservedInOtherCourts() {
        boolean found = false;
        for (List<Integer> otherCourt : otherCourts) {
            if (found2LaterSlotsThanReservedIn(otherCourt)) {
                found = true;
                break;
            }
        }
        return found;
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
        // fixme go through all analougous methods and fix them and write tests
        for (int i=aggregatedCourt.size()-1; i>=0; i--) {
            if (aggregatedCourt.get(i).equals(ORANGE)){
                lastBookedSlotPos = i;
                break;
            }
        }
        return  lastBookedSlotPos;
    }

    private void logRequestedExtensionButNoBookingFound() {
        String courtNameEng = Court.fromCourtId(courtId).name();
        System.out.printf("Requested %s for  %s in %s  court but no existing booking%n", extensionInterest, date, courtNameEng);
    }

}
