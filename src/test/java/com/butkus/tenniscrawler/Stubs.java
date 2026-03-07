package com.butkus.tenniscrawler;

import com.butkus.tenniscrawler.rest.SebFetcher;
import com.butkus.tenniscrawler.rest.timeinfobatch.AviableDuration;
import com.butkus.tenniscrawler.rest.timeinfobatch.DataTimeInfo;
import com.butkus.tenniscrawler.rest.timeinfobatch.TimeInfoBatchRspDto;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RequiredArgsConstructor
public class Stubs {

    private final SebFetcher fetcher;
    private final LocalDate day;

    static List<Desire> stubDesires(String date, ExtensionInterest extensionInterest, List<Long> courtIds) {
        List<Desire> desires = new ArrayList<>();
        desires.add(new Desire(LocalDate.parse(date), extensionInterest, courtIds));
        return desires;
    }

    static List<Desire> stubDesiresRecipe(String date, Recipe recipe) {
        List<Desire> desires = new ArrayList<>();
        desires.add(new Desire(LocalDate.parse(date), recipe));
        return desires;
    }

    public static TimeInfoBatchRspDto stubTimeInfoOccupied() {
        return new TimeInfoBatchRspDto().setStatus("success").setData(new ArrayList<>());
    }

    public static TimeInfoBatchRspDto stubTimeInfo(Long courtId, LocalDate date, LocalTime time, long durationMin) {
        List<DataTimeInfo> timeInfoData = new ArrayList<>();
        timeInfoData.add(stubDataTimeInfo(date, time, courtId, durationMin));
        return new TimeInfoBatchRspDto().setStatus("success").setData(timeInfoData);
    }

    public static TimeInfoBatchRspDto stubTimeInfo(LocalDate date, LocalTime time) {
        return stubTimeInfo(1L, date, time, 60L);
    }

    public static DataTimeInfo stubDataTimeInfo(LocalDate date, LocalTime time, long courtID, long durationMin) {
        List<AviableDuration> aviableDurations = new ArrayList<>();
        aviableDurations.add(new AviableDuration().setPrice("100 coins").setCourtID(courtID).setDurationMin(durationMin).setPriceWithDiscount("for you free"));

        return new DataTimeInfo()
                .setCourtID(courtID)
                .setDate(date.toString())
                .setTime(getTimeStringPadded(time))
                .setAviableDurations(aviableDurations);
    }

    private static @NonNull String getTimeStringPadded(LocalTime time) {
        String timeString = time.toString();
        if (timeString.matches("\\d{2}:\\d{2}")) {
            timeString += ":00";
        }
        return timeString;
    }

    public void stubOccupiedExcept(List<Long> requestedCourts, Court returnedCourt, LocalTime time, long vacancyDuration) {
        // in mocking, last mock matters. So, all are made to be empty, but then, if second mock is more specific, only second one will be in effect.
        when(fetcher.postTimeInfoBatch(any(), any(), any())).thenReturn(stubTimeInfoOccupied());

        // todo: add validation? requestedCourts and returnedCourt.getCourtId() should be from the same pool, e.g. indoorCourts, H01 or outdoorCourts, G01
        when(fetcher.postTimeInfoBatch(requestedCourts, day, time))
                .thenReturn(stubTimeInfo(returnedCourt.getCourtId(), day, time, vacancyDuration));
    }

}
