package com.butkus.tenniscrawler;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.butkus.tenniscrawler.Colors.ORANGE;
import static java.time.DayOfWeek.SUNDAY;

@UtilityClass
public class Calendar {

    private static final int MAX_CALENDAR_LINES = 8;     // 2 lines for headers + max 6 weeks of lines
    public static final Map<String, String> STRIKE_THROUGH_DIGITS =
            Map.of("1", "1̶", "2", "2̶", "3", "3̶", "4", "4̶", "5", "5̶", "6", "6̶", "7", "7̶", "8", "8̶", "9", "9̶", "0", "0̶");

    public static void printCalendar(Cache cache,
                                     List<Triplet<LocalDate, Integer, ExtensionInterest>> inputs) {
        int year = LocalDate.now().getYear();
        int currentMonth = LocalDate.now().getMonth().getValue();
        int nextMonth = currentMonth + 1;

        List<String> currentMonthLines = getPrintableMonth(cache, year, currentMonth, inputs);
        List<String> nextMonthLines = getPrintableMonth(cache, year, nextMonth, inputs);
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

    private static List<String> getPrintableMonth(Cache cache,
                                                  int year,
                                                  int month,
                                                  List<Triplet<LocalDate, Integer, ExtensionInterest>> inputs) {
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
            boolean booked = isBooked(cache, year, month, dayOfMonth);
            LocalDate currentDate = LocalDate.of(year, month, dayOfMonth);
            String dayString = getSymbolForTheDay(dayOfMonth, booked, currentDate, inputs);

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

    private static String getSymbolForTheDay(int dayOfMonth,
                                             boolean booked,
                                             LocalDate dateBeingProcessed,
                                             List<Triplet<LocalDate, Integer, ExtensionInterest>> inputs) {
        boolean skipped = inputs.stream()
                .filter(e -> dateBeingProcessed.equals(e.getValue0()))
                .anyMatch(e -> e.getValue2() == ExtensionInterest.NONE);
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

    private static boolean isBooked(Cache cache, int year, int month, int dayOfMonth) {
        for (CourtType court : CourtType.values()) {
            List<Integer> cachedCourt = cache.get(Pair.with(LocalDate.of(year, month, dayOfMonth), court.getCourtTypeId()));
            boolean foundInCourt = cachedCourt != null && cachedCourt.stream().anyMatch(e -> e.equals(ORANGE));
            if (foundInCourt) return true;
        }
        return false;
    }
}
