
package com.butkus.tenniscrawler.rest.placeinfobatch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@NoArgsConstructor
@Data
public class PlaceInfoBatchRspDto {

    @JsonProperty("data")
    private List<DataOuter> data;

    @JsonProperty("status")
    private String status;

    public boolean hasVacancies(String timeFrom, String timeTo) {
        LocalTime from = LocalTime.parse(timeFrom);
        LocalTime to = LocalTime.parse(timeTo);

        if (!"success".equals(status)) return false;

        // "place" a.k.a. courtType + data[]
        for (DataOuter outer : data) {
            // list of courtID + timetable
            for (List<DataInner> inner : outer.getData()) {
                // single courtID + timetable
                for (DataInner dataInner : inner) {
                    boolean hasVacancies = dataInner.getTimetable().hasVacancies(from, to);
                    if (hasVacancies) return true;
                }
            }
        }


        return false;
    }

}
