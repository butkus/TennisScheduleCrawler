
package com.butkus.tenniscrawler.rest.placeinfobatch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@Data
public class DataInner {

    @JsonProperty("courtID")
    private int courtID;

    @JsonProperty("courtName")
    private String courtName;

    @JsonProperty("date")
    private LocalDate date;

    @JsonProperty("timetable")
    private Timetable timetable;

}
