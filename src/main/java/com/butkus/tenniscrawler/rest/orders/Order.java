package com.butkus.tenniscrawler.rest.orders;

import com.butkus.tenniscrawler.Court;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalTime;

@Accessors(chain = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    private LocalDate date;
    private Court court;
    private LocalTime timeFrom;
    private LocalTime timeTo;

    public Order cloneWith(LocalTime requestedTimeTo) {
        return new Order()
                .setDate(this.date)
                .setCourt(this.court)
                .setTimeFrom(this.timeFrom)
                .setTimeTo(requestedTimeTo);
    }

    public Order clone() {
        return new Order()
                .setDate(this.date)
                .setCourt(this.court)
                .setTimeFrom(this.timeFrom)
                .setTimeTo(this.timeTo);
    }
}
