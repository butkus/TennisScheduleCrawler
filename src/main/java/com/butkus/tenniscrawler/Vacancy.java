package com.butkus.tenniscrawler;

import com.butkus.tenniscrawler.rest.SebFetcher;
import com.butkus.tenniscrawler.rest.orders.Order;
import com.butkus.tenniscrawler.rest.placeinfobatch.DataInner;
import com.butkus.tenniscrawler.rest.placeinfobatch.Timetable;
import com.butkus.tenniscrawler.rest.timeinfobatch.DataTimeInfo;
import com.butkus.tenniscrawler.rest.timeinfobatch.TimeInfoBatchRspDto;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjuster;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
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
    private Clock clock;

    private final Order order;

    private LocalDate day;
    private List<Long> courts;

    private final boolean isAny;
    private final boolean isEarlier;
    private final boolean isLater;

    private final boolean hasRecipe;
    private final Recipe recipe;

    // todo Vacancy builder to build main parts separtely, so that only params (or major ones) would be desire and order
    public Vacancy(Desire desire, BookingConfigurator bookingConfigurator) {
        this.earlyBird = bookingConfigurator.getEarlyBird();
        this.comfortable = bookingConfigurator.getComfortable();
        this.lateOwl = bookingConfigurator.getLateOwl();

        isAfterLateOwl = t -> t.isAfter(lateOwl);
        isBeforeEarlyBird = t -> t.isBefore(earlyBird);

        this.audioPlayer = bookingConfigurator.getAudioPlayer();
        this.fetcher = bookingConfigurator.getFetcher();
        this.clock = bookingConfigurator.getClock();

        this.order = desire.getOrder();

        day = desire.getDate();
        courts = desire.getCourts();

        isAny = desire.getExtensionInterest() == ANY;
        isEarlier = desire.getExtensionInterest() == EARLIER;
        isLater = desire.getExtensionInterest() == LATER;

        hasRecipe = desire.getRecipe() != null;
        recipe = desire.getRecipe();
    }

    public VacancyFound find(List<DataInner> courtDtos) {
        VacancyFound vacancyFound = null;

        if (hasRecipe) {
            vacancyFound = searchForReservationWithRecipe(courtDtos);
        } else if (isEarlier) {     // todo: earlier can remain as extension mechanism
            searchForEarlier();
        } else if (isLater) {       // todo: later can remain as extension mechanism
            searchForLater();
        } else if (isAny) {         // todo: any should be removed (recipe covers all slots).
            if (orderExists()) {
                boolean found = searchForEarlier();
                if (!found) {
                    searchForLater();
                }
            } else {
                repeatSearch(earlyBird, ADD_30_MIN, isAfterLateOwl);
            }
        }

        return vacancyFound;
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
        // todo: does the following have a does-order-exits check? Do we have a test for it?
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

    private VacancyFound searchForReservationWithRecipe(List<DataInner> courtDtos) {

        VacancyFound vacancyFound = iterateRecipes(courtDtos);

        if (vacancyFound != null) {
            System.out.printf("●●● New  %s %s - %s (courtId: %s) ●●●\n", day.toString(), vacancyFound.getFrom(), Court.getByCourtId(vacancyFound.getCourtId()), vacancyFound.getCourtId());

            audioPlayer.chimeIfNecessary();
            // todo should print what found and what order it improves.
        }

        return vacancyFound;
    }

    private VacancyFound iterateRecipes(List<DataInner> courtDtos) {
        boolean found = false;
        VacancyFound vacancyFound = null;
        int orderWeight = getOrderWeight();

        while (this.recipe.hasNext() && !found) {
            Map.Entry<Integer, List<CourtTypeAtHour>> currentRecipeWeightEntry = this.recipe.next();
            int currentRecipeWeight = currentRecipeWeightEntry.getKey();
            if (currentRecipeWeight < orderWeight) {
                    // just below this
                for (Integer duration : getViableDurations()) {
                    if (found) break;

                    // just below this
                    for (CourtTypeAtHour recipeCourtTypeAtHour : currentRecipeWeightEntry.getValue()) {
                        if (found) break;    // todo: refactor: both "continue and "break" are too much -- IT CAN ALSO BE "break" -- figure out why.
                        List<Long> recipeIds = recipeCourtTypeAtHour.getCourtType().getIds().stream().map(Court::getCourtId).toList();
                        LocalTime recipeFrom = recipeCourtTypeAtHour.getTime();

                        LocalTime recipeTo = recipeFrom.plusMinutes(duration);

                        for (DataInner courtDto : courtDtos) {
                            Timetable timetable = courtDto.getTimetable();
                            Long courtId = (long) courtDto.getCourtID();

                            boolean timeMatches = timetable.hasVacanciesExtended(recipeFrom, recipeTo);
                            boolean courtMatches = recipeIds.contains(courtId);
                            found = timeMatches && courtMatches;
                            if (found) {
                                // todo check how SEB forms request -- and then mimic it
                                // todo should not add `if volatile, then timeInfoBatch` because test for that is not yet
                                if (isVolatile()) {
                                    boolean found2 = searchForReservation(List.of(courtId), recipeFrom, MINUTES.between(recipeFrom, recipeTo));
                                    if (found2) {
                                        vacancyFound = new VacancyFound(courtId, day, recipeFrom, recipeTo);
                                    } else {
                                        found = false;
                                        // do not break;
                                    }
                                } else {
                                    vacancyFound = new VacancyFound(courtId, day, recipeFrom, recipeTo);
                                    break;  // todo: refactor: both "continue and "break" are too much
                                }

//                                vacancyFound = new VacancyFound(courtId, day, recipeFrom, recipeTo);
//                                break;  // todo: refactor: both "continue and "break" are too much
                            }
                        }
                    }

                }
            }
        }

        return vacancyFound;
    }

    private List<Integer> getViableDurations() {
        final int orderDuration;
        if (orderExists()) {
            orderDuration = (int) MINUTES.between(this.order.getTimeFrom(), this.order.getTimeTo());
        } else {
            orderDuration = 60;
        }
        return recipe.getDurationPreference().stream()
                .filter(duration -> duration >= orderDuration)
                .toList();
    }

    private int getOrderWeight() {
        int orderWeight = Integer.MAX_VALUE;
        if (orderExists()) {
            for (Map.Entry<Integer, List<CourtTypeAtHour>> entry : this.recipe.getMap().entrySet()) {
                for (CourtTypeAtHour courtTypeAtHour : entry.getValue()) {
                    LocalTime recipeTimeFrom = courtTypeAtHour.getTime();
                    Collection<Court> recipeIds = courtTypeAtHour.getCourtType().getIds();
                    Court orderCourtId = this.order.getCourt();
                    LocalTime orderTimeFrom = this.order.getTimeFrom();
                    boolean timeFromMatches = recipeTimeFrom.equals(orderTimeFrom);
                    boolean courtMatches = recipeIds.contains(orderCourtId);
                    if (timeFromMatches && courtMatches) {
                        return entry.getKey();
                    }
                }
            }
        }

        return orderWeight;
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

    public boolean isVolatile() {
        List<LocalDate> acceptableDates = List.of(
                LocalDate.now(clock),
                LocalDate.now(clock).plusDays(1),
                LocalDate.now(clock).plusDays(2)
        );
        return acceptableDates.contains(this.day);
    }
}
