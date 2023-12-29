
package com.butkus.tenniscrawler.rest.orders;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OrdersRspDto {

    @JsonProperty("data")
    private DataOrders data;

    @JsonProperty("status")
    private String status;

}
