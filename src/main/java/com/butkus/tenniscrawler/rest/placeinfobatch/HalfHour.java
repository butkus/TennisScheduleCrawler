
package com.butkus.tenniscrawler.rest.placeinfobatch;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalTime;

@Accessors(chain = true)
@Data
@NoArgsConstructor
public class HalfHour {

    private LocalTime from;
    private LocalTime to;
    private String status;
    private String info;    // e.g. https://ws.tenisopasaulis.lt/api/v1/timeInfo?courtID=54&date=2023-09-11&time=07%3A00%3A00

    public HalfHour(LocalTime from, LocalTime to, String status) {
        this.from = from;
        this.to = to;
        this.status = status;
    }
}
