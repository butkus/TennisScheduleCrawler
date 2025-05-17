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
import java.util.stream.Collectors;

import static com.butkus.tenniscrawler.ExtensionInterest.*;
import static java.time.temporal.ChronoUnit.MINUTES;

// todo rename to Vacancy? I used that word in tests, and it may just be more intuitive
public class Prospect {

    public static final TemporalAdjuster ADD_30_MIN = t -> t.plus(30L, MINUTES);
    public static final TemporalAdjuster SUBTRACT_30_MIN = t -> t.minus(30L, MINUTES);
    private final LocalTime earlyBird;
    private final LocalTime comfortable;
    private final LocalTime lateOwl;

    private final Predicate<LocalTime> isAfterLateOwl;
    private final Predicate<LocalTime> isBeforeEarlyBird;

    private AudioPlayer audioPlayer;
    private SebFetcher fetcher;

    private List<Order> orders;
    private LocalDate day;
    private List<Long> courts;

    private final boolean isAny;
    private final boolean isEarlier;
    private final boolean isLater;

    // todo prospect builder to build main parts separtely, so that only params (or major ones) would be desire and order
    public Prospect(Desire desire, List<Order> orders, BookingConfigurator bookingConfigurator) {
        this.earlyBird = bookingConfigurator.getEarlyBird();
        this.comfortable = bookingConfigurator.getComfortable();
        this.lateOwl = bookingConfigurator.getLateOwl();

        isAfterLateOwl = t -> t.isAfter(lateOwl);
        isBeforeEarlyBird = t -> t.isBefore(earlyBird);

        this.audioPlayer = bookingConfigurator.getAudioPlayer();
        this.fetcher = bookingConfigurator.getFetcher();

        // todo maybe make DesiresIteratorThingy retrieve order for the day and then this current class would not need to have getOrder() and orderExists() -- or have simplified versions of those methods.
        //  After all, what purpose does DesiresIteratorThingy have if not iterate though the desires (and prepare everything as well as possible so that Prospect would not need to do extra work).
        //  Alternatively, maybe DesiresIteratorThingy does not need to exist?
        this.orders = orders;

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
        boolean found = searchForReservation(getCourtId(), getOrderFromMinus30Min(), 30L);      // tries to extend (regardless if e.g. desired court is HARD instead of CLAY)
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
        List<Order> matchingOrder = this.orders.stream()
                .filter(e -> e.getDate().equals(this.day))
                .collect(Collectors.toList());
        return !matchingOrder.isEmpty();
    }

    private Order getOrder() {
        List<Order> matchingOrder = this.orders.stream()
                .filter(e -> e.getDate().equals(this.day))
                .collect(Collectors.toList());
        if (matchingOrder.isEmpty()) throw new RuntimeException("searched for reservation, but no reservation found for " + this.day);
        if (matchingOrder.size() > 1) throw new RuntimeException("multiple reservation per day handling not implemented. Day = " + this.day);
        return matchingOrder.get(0);
    }

    private List<Long> getCourtId() {
        return List.of(getOrder().getCourt().getCourtId());
    }

    private LocalTime getOrderFromMinus30Min() {
        return getOrder().getTimeFrom().minusMinutes(30);
    }

    private LocalTime getOrderToMinus30Min() {
        return getOrder().getTimeTo().minusMinutes(30);
    }

    private long getOrderDurationOrDefault() {
        if (orderExists()) {
            Order order = getOrder();
            return MINUTES.between(order.getTimeFrom(), order.getTimeTo());
        } else {
            return 60L;
        }
    }

    private LocalTime getOrderTimeTo() {
        return getOrder().getTimeTo();
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
