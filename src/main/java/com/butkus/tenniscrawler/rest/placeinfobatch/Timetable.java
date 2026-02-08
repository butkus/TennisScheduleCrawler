package com.butkus.tenniscrawler.rest.placeinfobatch;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Accessors(chain = true)
@Data
@NoArgsConstructor
public class Timetable {

    @EqualsAndHashCode.Exclude
    private List<HalfHour> all = new ArrayList<>();

    @JsonProperty("06:00:00")
    private HalfHour t0600;
    @JsonProperty("06:30:00")
    private HalfHour t0630;
    @JsonProperty("07:00:00")
    private HalfHour t0700;
    @JsonProperty("07:30:00")
    private HalfHour t0730;
    @JsonProperty("08:00:00")
    private HalfHour t0800;
    @JsonProperty("08:30:00")
    private HalfHour t0830;
    @JsonProperty("09:00:00")
    private HalfHour t0900;
    @JsonProperty("09:30:00")
    private HalfHour t0930;
    @JsonProperty("10:00:00")
    private HalfHour t1000;
    @JsonProperty("10:30:00")
    private HalfHour t1030;
    @JsonProperty("11:00:00")
    private HalfHour t1100;
    @JsonProperty("11:30:00")
    private HalfHour t1130;
    @JsonProperty("12:00:00")
    private HalfHour t1200;
    @JsonProperty("12:30:00")
    private HalfHour t1230;
    @JsonProperty("13:00:00")
    private HalfHour t1300;
    @JsonProperty("13:30:00")
    private HalfHour t1330;
    @JsonProperty("14:00:00")
    private HalfHour t1400;
    @JsonProperty("14:30:00")
    private HalfHour t1430;
    @JsonProperty("15:00:00")
    private HalfHour t1500;
    @JsonProperty("15:30:00")
    private HalfHour t1530;
    @JsonProperty("16:00:00")
    private HalfHour t1600;
    @JsonProperty("16:30:00")
    private HalfHour t1630;
    @JsonProperty("17:00:00")
    private HalfHour t1700;
    @JsonProperty("17:30:00")
    private HalfHour t1730;
    @JsonProperty("18:00:00")
    private HalfHour t1800;
    @JsonProperty("18:30:00")
    private HalfHour t1830;
    @JsonProperty("19:00:00")
    private HalfHour t1900;
    @JsonProperty("19:30:00")
    private HalfHour t1930;
    @JsonProperty("20:00:00")
    private HalfHour t2000;
    @JsonProperty("20:30:00")
    private HalfHour t2030;
    @JsonProperty("21:00:00")
    private HalfHour t2100;
    @JsonProperty("21:30:00")
    private HalfHour t2130;
    @JsonProperty("22:00:00")
    private HalfHour t2200;
    @JsonProperty("22:30:00")
    private HalfHour t2230;

    private void populateAll() {
        this.all = new ArrayList<>();
        all.add(t0600);
        all.add(t0630);
        all.add(t0700);
        all.add(t0730);
        all.add(t0800);
        all.add(t0830);
        all.add(t0900);
        all.add(t0930);
        all.add(t1000);
        all.add(t1030);
        all.add(t1100);
        all.add(t1130);
        all.add(t1200);
        all.add(t1230);
        all.add(t1300);
        all.add(t1330);
        all.add(t1400);
        all.add(t1430);
        all.add(t1500);
        all.add(t1530);
        all.add(t1600);
        all.add(t1630);
        all.add(t1700);
        all.add(t1730);
        all.add(t1800);
        all.add(t1830);
        all.add(t1900);
        all.add(t1930);
        all.add(t2000);
        all.add(t2030);
        all.add(t2100);
        all.add(t2130);
        all.add(t2200);
        all.add(t2230);

        all.removeIf(Objects::isNull);
    }

    public boolean hasVacancies(LocalTime requestedFrom, LocalTime requestedTo) {
        populateAll();
        for (HalfHour halfHour : all) {
            LocalTime currentFrom = halfHour.getFrom();
            LocalTime currentTo = halfHour.getTo();
            boolean isOkFrom = !currentFrom.isBefore(requestedFrom);
            boolean isOkTo = !currentTo.isAfter(requestedTo);
            boolean isStatusFree = halfHour.getStatus().equals("free"); // possible values: "full", "fullsell", "free"
            boolean isFree = isOkFrom && isOkTo && isStatusFree;
            if (isFree) {
                System.out.println("halfHour = " + halfHour);
                return true;
            }
        }
        return false;
    }

    public boolean hasVacanciesExtended(LocalTime requestedFrom, LocalTime requestedTo) {
        populateAll();

        int startPositionFound = -1;
        for (int i = 0; i < all.size(); i++) {
            HalfHour halfHour = all.get(i);
            if (halfHour.getFrom().equals(requestedFrom)) {
                startPositionFound = all.indexOf(halfHour);
            }
        }

        int endPositionFound = -1;
        for (int i = all.size()-1; i >= 0; i--) {
            HalfHour halfHour = all.get(i);
            if (halfHour.getTo().equals(requestedTo)) {
                endPositionFound = all.indexOf(halfHour);
            }
        }

        boolean fromAndToFound = startPositionFound != -1 && endPositionFound != -1;
        if (!fromAndToFound) {
            return false;
        }

        boolean chainUnbroken = true;
        List<String> sellable = List.of("free", "fullsell");
        for (int i = startPositionFound; i <= endPositionFound; i++) {
            HalfHour current = all.get(i);
            if (!sellable.contains(current.getStatus())) {
                chainUnbroken = false;
                break;
            }
        }
        return chainUnbroken;
    }
}
