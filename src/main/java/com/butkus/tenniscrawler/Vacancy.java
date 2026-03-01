package com.butkus.tenniscrawler;

import com.butkus.tenniscrawler.rest.orders.Order;
import com.butkus.tenniscrawler.rest.placeinfobatch.DataInner;
import com.butkus.tenniscrawler.rest.placeinfobatch.Timetable;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.butkus.tenniscrawler.ExtensionInterest.*;
import static java.time.temporal.ChronoUnit.MINUTES;

public class Vacancy {

    private final AudioPlayer audioPlayer;
    private final Clock clock;

    private final Order order;
    private final LocalDate day;

    private final boolean isAny;
    private final boolean isEarlier;
    private final boolean isLater;

    private final boolean hasRecipe;
    private final Recipe recipe;

    private final LegacySearch legacySearch;

    public Vacancy(Desire desire, BookingConfigurator bookingConfigurator) {
        this.audioPlayer = bookingConfigurator.getAudioPlayer();
        this.clock = bookingConfigurator.getClock();

        this.order = desire.getOrder();
        this.day = desire.getDate();

        isAny = desire.getExtensionInterest() == ANY;
        isEarlier = desire.getExtensionInterest() == EARLIER;
        isLater = desire.getExtensionInterest() == LATER;

        hasRecipe = desire.getRecipe() != null;
        recipe = desire.getRecipe();

        this.legacySearch = new LegacySearch(bookingConfigurator, desire);
    }

    public VacancyFound find(List<DataInner> courtDtos) {
        VacancyFound vacancyFound = null;

        if (hasRecipe) {
            vacancyFound = searchForReservationWithRecipe(courtDtos);
        } else if (isEarlier) {     // todo: earlier can remain as extension mechanism
            legacySearch.searchForEarlier();
        } else if (isLater) {       // todo: later can remain as extension mechanism
            legacySearch.searchForLater();
        } else if (isAny) {         // todo: any SHOULD BE REMOVED (recipe covers all slots).
            // todo add unsupported exception
        }

        // todo print found court here
        //   System.out.printf("●●● New  .....
        return vacancyFound;
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
                                    boolean found2 = legacySearch.searchForReservation(List.of(courtId), recipeFrom, MINUTES.between(recipeFrom, recipeTo));
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

    private boolean orderExists() {
        return this.order != null;
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
