
package com.butkus.tenniscrawler.rest.timeinfobatch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Accessors(chain = true)
@lombok.Data
public class DataTimeInfo {

    @JsonProperty("aviableDurations")
    private List<AviableDuration> aviableDurations = new ArrayList<>();
    @JsonProperty("courtID")
    private Long courtID;
    @JsonProperty("courtName")
    private String courtName;
    @JsonProperty("date")
    private String date;
    @JsonProperty("placeName")
    private String placeName;
    @JsonProperty("time")
    private String time;

    public Long findFirstAcceptableDuration(Long minimumAcceptableDuration) {
        for (AviableDuration aviableDuration : aviableDurations) {
            Long duration = aviableDuration.getDurationMin();
            if (duration >= minimumAcceptableDuration) {
                return duration;
            }
        }
        return null;
    }

}
