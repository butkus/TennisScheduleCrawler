
package com.butkus.tenniscrawler.rest.placeinfobatch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Accessors(chain = true)
@Data
public class PlaceInfoBatchRqstDto {

    @JsonProperty("dates")
    private List<String> dates;

    /**
     * e.g. BS 11 sint. žolė
     */
    @JsonProperty("excludeCourtName")
    private Boolean excludeCourtName;

    /**
     * e.g. https://ws.tenisopasaulis.lt/api/v1/timeInfo?courtID=54&date=2023-09-11&time=07%3A00%3A00
     */
    @JsonProperty("excludeInfoUrl")
    private Boolean excludeInfoUrl;

    @JsonProperty("places")
    private List<Integer> places;

    @JsonProperty("salePoint")
    private Long salePoint;

    @JsonProperty("sessionToken")
    private String sessionToken;

}
