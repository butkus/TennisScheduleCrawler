
package com.butkus.tenniscrawler.rest.timeinfobatch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.experimental.Accessors;

import java.util.List;

@Accessors(chain = true)
@lombok.Data
public class TimeInfoBatchRspDto {

    @JsonProperty("data")
    private List<DataTimeInfo> data;
    @JsonProperty("status")
    private String status;

    // todo check if used in all places where applicable
    public void validate() {
        if (!"success".equals(getStatus())) {
            throw new RuntimeException("--- error on 'postTimeInfoBatch', response is not 'success'");
        }
    }

}
