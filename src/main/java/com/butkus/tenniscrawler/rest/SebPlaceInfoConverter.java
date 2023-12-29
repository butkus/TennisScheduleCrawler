package com.butkus.tenniscrawler.rest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SebPlaceInfoConverter {


    public static List<LocalDate> getDateRange(String dateFrom, String dateTo) {
        LocalDate from = LocalDate.parse(dateFrom);
        LocalDate to = LocalDate.parse(dateTo);

        List<LocalDate> result = new ArrayList<>();
        if (from.isAfter(to)) return result;

        for (int i = 0;;i++) {
            LocalDate current = from.plusDays(i);
            if (!current.isAfter(to)) {
                result.add(current);
            } else {
                break;
            }
        }

        return result;
    }
}
