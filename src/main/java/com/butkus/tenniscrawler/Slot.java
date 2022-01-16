package com.butkus.tenniscrawler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Accessors(chain = true)
@Data
public class Slot {
    private final String classes;
    private final String courtNo;
    private final String time;
}
