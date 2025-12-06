package com.butkus.tenniscrawler;

import com.butkus.tenniscrawler.rest.SebFetcher;
import com.butkus.tenniscrawler.rest.orders.Order;
import com.butkus.tenniscrawler.rest.timeinfobatch.DataTimeInfo;
import com.butkus.tenniscrawler.rest.timeinfobatch.TimeInfoBatchRspDto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjuster;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static com.butkus.tenniscrawler.ExtensionInterest.*;
import static java.time.temporal.ChronoUnit.MINUTES;

public class Vacancy {

    public static final TemporalAdjuster ADD_30_MIN = t -> t.plus(30L, MINUTES);
    public static final TemporalAdjuster SUBTRACT_30_MIN = t -> t.minus(30L, MINUTES);
    private final LocalTime earlyBird;
    private final LocalTime comfortable;
    private final LocalTime lateOwl;

    private final Predicate<LocalTime> isAfterLateOwl;
    private final Predicate<LocalTime> isBeforeEarlyBird;

    private AudioPlayer audioPlayer;
    private SebFetcher fetcher;

    private final Order order;

    private LocalDate day;
    private List<Long> courts;

    private final boolean isAny;
    private final boolean isEarlier;
    private final boolean isLater;

    // todo Vacancy builder to build main parts separtely, so that only params (or major ones) would be desire and order
    public Vacancy(Desire desire, BookingConfigurator bookingConfigurator) {
        this.earlyBird = bookingConfigurator.getEarlyBird();
        this.comfortable = bookingConfigurator.getComfortable();
        this.lateOwl = bookingConfigurator.getLateOwl();

        isAfterLateOwl = t -> t.isAfter(lateOwl);
        isBeforeEarlyBird = t -> t.isBefore(earlyBird);

        this.audioPlayer = bookingConfigurator.getAudioPlayer();
        this.fetcher = bookingConfigurator.getFetcher();

        this.order = desire.getOrder();

        day = desire.getDate();
        courts = desire.getCourts();

        isAny = desire.getExtensionInterest() == ANY;
        isEarlier = desire.getExtensionInterest() == EARLIER;
        isLater = desire.getExtensionInterest() == LATER;
    }

    public void find() {
        if (isEarlier) {
            searchForEarlier();
        } else if (isLater) {
            searchForLater();
        } else if (isAny) {
            if (orderExists()) {
                boolean found = searchForEarlier();
                if (!found) {
                    searchForLater();
                }
            } else {
                repeatSearch(earlyBird, ADD_30_MIN, isAfterLateOwl);
            }
        }
    }

    private void searchForLater() {
        // extend existing reservation
        boolean found = searchForReservation(getCourtId(), getOrderTimeTo(), 30L);
        if (!found) {
            // find brand-new reservation
            repeatSearch(getOrderToMinus30Min(), ADD_30_MIN, isAfterLateOwl);
        }
    }

    private boolean searchForEarlier() {
        // extend existing reservation
        boolean found = searchForReservation(getCourtId(), getOrderFromMinus30Min(), 30L);      // tries to extend (regardless if e.g. desired court is HARD instead of CLAY). [LATER COMMENT ->] IS IT STILL TRUE? A CONCRETE COURT ID IS PASSED
        if (!found) {
            // find brand-new reservation
            found = repeatSearch(getOrderFromMinus30Min(), SUBTRACT_30_MIN, isBeforeEarlyBird);
        }
        return found;
    }


    private boolean searchForReservation(List<Long> courts, LocalTime time, long minimumAcceptableDuration) {
        TimeInfoBatchRspDto response = fetcher.postTimeInfoBatch(courts, this.day, time);
        response.validate();
        return searchForTime(response, minimumAcceptableDuration);
    }

    private static List<Long> getCourtsWithoutBookedCourt(List<Long> courts, Order order) {
        List<Long> courtListWithoutBookedCourtId = new ArrayList<>(courts);
        courtListWithoutBookedCourtId.remove(order.getCourt().getCourtId());
        return courtListWithoutBookedCourtId;
    }

    private boolean searchForTime(TimeInfoBatchRspDto timeResp, long minimumAcceptableDuration) {
        boolean found = false;
        for (DataTimeInfo datum : timeResp.getData()) {
            if (datum.hasDuration(minimumAcceptableDuration)) {
                found = true;
                String timeString = datum.getTime().substring(0, datum.getTime().length() - 3);
                System.out.println("●●● New  " + datum.getDate() + " " + timeString + "  " + datum.getCourtName() + " ●●●");
                audioPlayer.chimeIfNecessary();
            }
        }
        return found;
    }

    private boolean orderExists() {
        return this.order != null;
    }

    private List<Long> getCourtId() {
        return List.of(this.order.getCourt().getCourtId());
    }

    private LocalTime getOrderFromMinus30Min() {
        return this.order.getTimeFrom().minusMinutes(30);
    }

    private LocalTime getOrderToMinus30Min() {
        return this.order.getTimeTo().minusMinutes(30);
    }

    private long getOrderDurationOrDefault() {
        if (orderExists()) {
            return MINUTES.between(order.getTimeFrom(), order.getTimeTo());
        } else {
            return 60L;
        }
    }

    private LocalTime getOrderTimeTo() {
        return this.order.getTimeTo();
    }

    private boolean repeatSearch(LocalTime initial, TemporalAdjuster next, Predicate<LocalTime> searchUntil) {
        boolean found;
        boolean enough;
        LocalTime time = initial;
        do {
            found = searchForReservation(courts, time, getOrderDurationOrDefault());
            time = time.with(next);
            enough = found || searchUntil.test(time);
        } while (!enough);

        return found;
    }

}
