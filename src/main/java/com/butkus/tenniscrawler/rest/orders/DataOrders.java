
package com.butkus.tenniscrawler.rest.orders;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class DataOrders {

    @JsonProperty("from")
    private String from;

    @JsonProperty("results")
    private List<Result> results;

    @JsonProperty("to")
    private String to;

}
