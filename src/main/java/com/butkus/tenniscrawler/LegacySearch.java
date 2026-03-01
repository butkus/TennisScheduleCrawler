package com.butkus.tenniscrawler;

import com.butkus.tenniscrawler.rest.SebFetcher;
import com.butkus.tenniscrawler.rest.orders.Order;
import com.butkus.tenniscrawler.rest.timeinfobatch.DataTimeInfo;
import com.butkus.tenniscrawler.rest.timeinfobatch.TimeInfoBatchRspDto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjuster;
import java.util.List;
import java.util.function.Predicate;

import static java.time.temporal.ChronoUnit.MINUTES;

public class LegacySearch {

    public static final TemporalAdjuster ADD_30_MIN = t -> t.plus(30L, MINUTES);
    public static final TemporalAdjuster SUBTRACT_30_MIN = t -> t.minus(30L, MINUTES);

    private final AudioPlayer audioPlayer;
    private final SebFetcher fetcher;

    private final LocalDate day;
    private final List<Long> courts;
    private final Order order;

    private final LocalTime earlyBird;
    private final Predicate<LocalTime> isBeforeEarlyBird;
    private final Predicate<LocalTime> isAfterLateOwl;

    public LegacySearch(BookingConfigurator bookingConfigurator, Desire desire) {
        this.audioPlayer = bookingConfigurator.getAudioPlayer();
        this.fetcher = bookingConfigurator.getFetcher();
        this.day = desire.getDate();
        this.courts = desire.getCourts();
        this.order = desire.getOrder();
        this.earlyBird = bookingConfigurator.getEarlyBird();
        this.isBeforeEarlyBird = t -> t.isBefore(bookingConfigurator.getEarlyBird());
        this.isAfterLateOwl = t -> t.isAfter(bookingConfigurator.getLateOwl());
    }

    public boolean searchForEarlier() {
        // extend existing reservation
        // todo: does the following have a does-order-exits check? Do we have a test for it?
        boolean found = searchForReservation(getCourtId(), getOrderFromMinus30Min(), 30L);
        if (!found) {
            // find brand-new reservation
            found = repeatSearch(getOrderFromMinus30Min(), SUBTRACT_30_MIN, isBeforeEarlyBird);
        }
        return found;
    }

    public void searchForLater() {
        // extend existing reservation
        boolean found = searchForReservation(getCourtId(), order.getTimeTo(), 30L);
        if (!found) {
            // find brand-new reservation
            repeatSearch(getOrderToMinus30Min(), ADD_30_MIN, isAfterLateOwl);
        }
    }

    public boolean searchForReservation(List<Long> courts, LocalTime time, long minimumAcceptableDuration) {
        TimeInfoBatchRspDto response = fetcher.postTimeInfoBatch(courts, day, time);
        response.validate();
        return searchForTime(response, minimumAcceptableDuration);
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


    long getOrderDurationOrDefault() {
        if (orderExists()) {
            return MINUTES.between(order.getTimeFrom(), order.getTimeTo());
        } else {
            return 60L;
        }
    }

    List<Long> getCourtId() { return List.of(this.order.getCourt().getCourtId()); }

    LocalTime getOrderFromMinus30Min() { return this.order.getTimeFrom().minusMinutes(30); }

    LocalTime getOrderToMinus30Min() { return this.order.getTimeTo().minusMinutes(30); }

    // fixme; NOT DRY -- same method exists in Vacancy

    private boolean orderExists() {
        return this.order != null;
    }

    public void searchForNewReservation() {
        repeatSearch(earlyBird, ADD_30_MIN, isAfterLateOwl);
    }

}
