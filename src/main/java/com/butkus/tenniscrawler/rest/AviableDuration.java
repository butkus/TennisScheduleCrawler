
package com.butkus.tenniscrawler.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AviableDuration {

    @JsonProperty("courtID")
    private Long courtID;

    @JsonProperty("duration_min")
    private Long durationMin;

    @JsonProperty("price")
    private String price;

    @JsonProperty("price_with_discount")
    private String priceWithDiscount;

}
