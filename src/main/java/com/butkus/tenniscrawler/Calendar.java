package com.butkus.tenniscrawler;

import com.butkus.tenniscrawler.rest.orders.Order;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static java.time.DayOfWeek.SUNDAY;

@UtilityClass
public class Calendar {

    private static final int MAX_CALENDAR_LINES = 8;     // 2 lines for headers + max 6 weeks of lines
    public static final Map<String, String> STRIKE_THROUGH_DIGITS =
            Map.of("1", "1̶", "2", "2̶", "3", "3̶", "4", "4̶", "5", "5̶", "6", "6̶", "7", "7̶", "8", "8̶", "9", "9̶", "0", "0̶");

    public static void printCalendar(List<Order> orders, List<Desire> desires) {
        int year = LocalDate.now().getYear();
        int currentMonth = LocalDate.now().getMonth().getValue();
        int nextMonth = currentMonth + 1;

        // todo redo all year, month, day integers to LocalDate or something like this-global-month, next-global-month data structures (month containing which year it belongs to)

        int yearForNextMonth = year;
        if (nextMonth == 13) {
            yearForNextMonth = year + 1;
            nextMonth = 1;
        }

        List<String> currentMonthLines = getPrintableMonth(orders, year, currentMonth, desires);
        List<String> nextMonthLines = getPrintableMonth(orders, yearForNextMonth, nextMonth, desires);
        List<String> bothMonths = new ArrayList<>();
        String emptyCalendarLine = " ".repeat(currentMonthLines.get(0).length());
        for (int i = 0; i < MAX_CALENDAR_LINES; i++) {
            String left = currentMonthLines.size() - 1 < i ? emptyCalendarLine : currentMonthLines.get(i);
            String right = nextMonthLines.size() - 1 < i ? emptyCalendarLine : nextMonthLines.get(i);
            bothMonths.add(left + "      " + right);
        }

        System.out.println();
        for (String lineByLine : bothMonths) {
            System.out.println(lineByLine);
        }
    }

    private static List<String> getPrintableMonth(List<Order> orders, int year, int month, List<Desire> desires) {
        List<String> lines = new ArrayList<>();

        YearMonth yearMonth = YearMonth.of(year, month);

        String headers = " P   A   T   K   P   Š   S";
        String ltMonth = Translations.getLtMonth(yearMonth);
        lines.add(StringUtils.center(ltMonth, headers.length()));
        lines.add(headers);

        int startOfMonthWhitespaceCount = LocalDate.of(year, month, 1).getDayOfWeek().getValue() - 1;
        String accum = "    ".repeat(startOfMonthWhitespaceCount);

        DayOfWeek dayOfWeek = null;
        for (int dayOfMonth = 1; dayOfMonth <= yearMonth.lengthOfMonth(); dayOfMonth++) {
            boolean booked = isBooked(orders, year, month, dayOfMonth);
            LocalDate currentDate = LocalDate.of(year, month, dayOfMonth);
            String dayString = getSymbolForTheDay(dayOfMonth, booked, currentDate, desires);

            accum += String.format("%2s  ", dayString);

            dayOfWeek = currentDate.getDayOfWeek();
            if (dayOfWeek == SUNDAY) {
                lines.add(accum.substring(0, accum.length() - 2));
                accum = "";
            }
        }
        int daysTillEndOfWeek = SUNDAY.getValue() - dayOfWeek.getValue();
        accum += "    ".repeat(daysTillEndOfWeek);
        if (accum.isEmpty()) accum = "==";  // todo solve why it is empty (was empty on 2023-12-31)
        lines.add(accum.substring(0, accum.length() - 2));

        return lines;
    }

    private static String getSymbolForTheDay(int dayOfMonth,
                                             boolean booked,
                                             LocalDate dateBeingProcessed,
                                             List<Desire> desires) {

        boolean skipped = desires.stream()
                .filter(e -> e.getDate().equals(dateBeingProcessed))
                .anyMatch(e -> e.getExtensionInterest() == ExtensionInterest.NONE);

        if (dateBeingProcessed.isBefore(LocalDate.now())) {
            return "░░";
        } else if (booked) {
            return " ●";
        } else if (skipped) {
            String day = Integer.toString(dayOfMonth);
            int prependedSpacesNeeded = 2 - day.length();
            for (Map.Entry<String, String> entry : STRIKE_THROUGH_DIGITS.entrySet()) {
                day = day.replace(entry.getKey(), entry.getValue());
            }
            return " ".repeat(prependedSpacesNeeded) + day;
        } else {
            return Integer.toString(dayOfMonth);
        }
    }

    private static boolean isBooked(List<Order> orders, int year, int month, int dayOfMonth) {
        Predicate<Order> sameDate = e -> e.getDate().getYear() == year &&
                e.getDate().getMonth().getValue() == month &&
                e.getDate().getDayOfMonth() == dayOfMonth;
        return orders.stream().anyMatch(sameDate);
    }
}
