
package com.butkus.tenniscrawler.rest.timeinfobatch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Accessors(chain = true)
@NoArgsConstructor
@Data
public class TimeInfoBatchRqstDto {

    @JsonProperty("courts")
    private List<Long> courts;

    @JsonProperty("date")
    private String date;

    @JsonProperty("salePoint")
    private Long salePoint;

    @JsonProperty("sessionToken")
    private String sessionToken;

    @JsonProperty("time")
    private String time;

}
