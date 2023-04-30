
package com.butkus.tenniscrawler.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@lombok.Data
public class AvailableCourtsRspDto {

    @JsonProperty("data")
    private List<Data> data;
    @JsonProperty("status")
    private String status;

}
