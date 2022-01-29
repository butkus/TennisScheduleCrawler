package com.butkus.tenniscrawler;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.javatuples.Pair;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import static com.butkus.tenniscrawler.Colors.ORANGE;
import static com.butkus.tenniscrawler.Court.CARPET;
import static com.butkus.tenniscrawler.Court.HARD;
import static java.time.DayOfWeek.SUNDAY;

@UtilityClass
public class Calendar {

    private static final int MAX_CALENDAR_LINES = 8;     // 2 lines for headers + max 6 weeks of lines

    public static void printCalendar(Cache cache) {
        int year = LocalDate.now().getYear();
        int currentMonth = LocalDate.now().getMonth().getValue();
        int nextMonth = currentMonth + 1;

        List<String> currentMonthLines = getPrintableMonth(cache, year, currentMonth);
        List<String> nextMonthLines = getPrintableMonth(cache, year, nextMonth);
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
        System.out.println();
    }

    private static List<String> getPrintableMonth(Cache cache, int year, int month) {
        List<String> lines = new ArrayList<>();

        YearMonth yearMonth = YearMonth.of(year, month);

        String headers = " P   A   T   K   P   Š   S";
        String ltMonth = Months.getLtMonths().get(yearMonth.getMonth());
        lines.add(StringUtils.center(ltMonth, headers.length()));
        lines.add(headers);

        int startOfMonthWhitespaceCount = LocalDate.of(year, month, 1).getDayOfWeek().getValue() - 1;
        String accum = "    ".repeat(startOfMonthWhitespaceCount);

        DayOfWeek dayOfWeek = null;
        for (int dayOfMonth = 1; dayOfMonth <= yearMonth.lengthOfMonth(); dayOfMonth++) {
            boolean booked = isBooked(cache, year, month, dayOfMonth);
            LocalDate currentDate = LocalDate.of(year, month, dayOfMonth);
            String dayString = getSymbolForTheDay(dayOfMonth, booked, currentDate);

            accum += String.format("%2s  ", dayString);

            dayOfWeek = currentDate.getDayOfWeek();
            if (dayOfWeek == SUNDAY) {
                lines.add(accum.substring(0, accum.length() - 2));
                accum = "";
            }
        }
        int daysTillEndOfWeek = SUNDAY.getValue() - dayOfWeek.getValue();
        accum += "    ".repeat(daysTillEndOfWeek);
        lines.add(accum.substring(0, accum.length() - 2));

        return lines;
    }

    private static String getSymbolForTheDay(int dayOfMonth, boolean booked, LocalDate dateBeingProcessed) {
        if (dateBeingProcessed.isBefore(LocalDate.now())) {
            return "░░";
        } else if (booked) {
            return " ●";
        } else {
            return Integer.toString(dayOfMonth);
        }
    }

    private static boolean isBooked(Cache cache, int year, int month, int dayOfMonth) {
        List<Integer> cacheHard = cache.get(Pair.with(LocalDate.of(year, month, dayOfMonth), HARD));
        List<Integer> cacheCarpet = cache.get(Pair.with(LocalDate.of(year, month, dayOfMonth), CARPET));
        boolean foundInHard = cacheHard != null && cacheHard.stream().anyMatch(e -> e.equals(ORANGE));
        boolean foundInCarpet = cacheCarpet != null && cacheCarpet.stream().anyMatch(e -> e.equals(ORANGE));
        return foundInHard || foundInCarpet;
    }
}
