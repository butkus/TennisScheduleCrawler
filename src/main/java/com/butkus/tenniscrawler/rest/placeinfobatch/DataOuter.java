
package com.butkus.tenniscrawler.rest.placeinfobatch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DataOuter {

    @JsonProperty("place")
    private int place;

    @JsonProperty("data")
    private List<List<DataInner>> data;

}
