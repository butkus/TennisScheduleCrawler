
package com.butkus.tenniscrawler.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Data {

    @JsonProperty("aviableDurations")
    private List<AviableDuration> aviableDurations;
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

}
