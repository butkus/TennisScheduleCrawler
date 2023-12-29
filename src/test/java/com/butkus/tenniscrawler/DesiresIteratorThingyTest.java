package com.butkus.tenniscrawler;

import com.butkus.tenniscrawler.rest.SebFetcher;
import com.butkus.tenniscrawler.rest.SebOrderConverter;
import com.butkus.tenniscrawler.rest.orders.Order;
import com.butkus.tenniscrawler.rest.timeinfobatch.AviableDuration;
import com.butkus.tenniscrawler.rest.timeinfobatch.DataTimeInfo;
import com.butkus.tenniscrawler.rest.timeinfobatch.TimeInfoBatchRspDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.butkus.tenniscrawler.ExtensionInterest.ANY;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DesiresIteratorThingyTest {

    @Mock private AudioPlayer audioPlayer;
    @Mock private SebFetcher fetcher;
    private BookingConfigurator configurator;

    private DesiresIteratorThingy thingy;
//    @InjectMocks private DesiresIteratorThingy thingy;

    private MockedStatic<SebOrderConverter> orderConverterMockedStatic;

    @Captor private ArgumentCaptor<List<Long>> courtsCaptor;
    @Captor private ArgumentCaptor<LocalDate> dateCaptor;
    @Captor private ArgumentCaptor<LocalTime> timeCaptor;

    @BeforeEach
    void setUp() {
        orderConverterMockedStatic = mockStatic(SebOrderConverter.class);
        configurator = new BookingConfigurator(audioPlayer, fetcher);
        thingy = new DesiresIteratorThingy(configurator);
    }

    @AfterEach
    void tearDown() {
        orderConverterMockedStatic.close();
    }

    private static List<Order> stubOrders(Court court, String date, String timeFrom, String timeTo) {
        List<Order> orders = new ArrayList<>();
        orders.add(new Order(LocalDate.parse(date), court, LocalTime.parse(timeFrom), LocalTime.parse(timeTo)));
        return orders;
    }

    private static List<Order> stubOrders(String date, String timeFrom, String timeTo) {
        return stubOrders(Court.H01, date, timeFrom, timeTo);
    }

    private static TimeInfoBatchRspDto stubTimeInfoEmpty() {
        return new TimeInfoBatchRspDto().setStatus("success").setData(new ArrayList<>());
    }

    private static TimeInfoBatchRspDto stubTimeInfo(Long courtId, LocalDate date, LocalTime time, long durationMin) {
        List<DataTimeInfo> timeInfoData = new ArrayList<>();
        timeInfoData.add(stubDataTimeInfo(date, time, courtId, durationMin));
        return new TimeInfoBatchRspDto().setStatus("success").setData(timeInfoData);
    }

    private static TimeInfoBatchRspDto stubTimeInfo(LocalDate date, LocalTime time) {
        return stubTimeInfo(1L, date, time, 60L);
    }

    private static DataTimeInfo stubDataTimeInfo(LocalDate date, LocalTime time, long courtID, long durationMin) {
        List<AviableDuration> aviableDurations = new ArrayList<>();
        aviableDurations.add(new AviableDuration().setPrice("100 coins").setCourtID(courtID).setDurationMin(durationMin).setPriceWithDiscount("for you free"));
        DataTimeInfo dataTimeInfo = new DataTimeInfo().setCourtID(courtID).setDate(date.toString()).setTime(time.toString()).setAviableDurations(aviableDurations);
        return dataTimeInfo;
    }



    @Test
    void requestedAny_noPriorOrders_nothingExists_searches_4_timesAndDoesNotFind() {
        // todo do not check at all when optimization is in place,
        //  i.e. postPlaceInfoBatch will determine no free slots and no need for postTimeInfoBatch to search

        mockOrders(new ArrayList<>());
        List<Desire> desires = stubDesires("2023-10-01", ANY, Court.getClayIds());

        // whatever you ask, there's NO booking
        when(fetcher.postTimeInfoBatch(any(), any(), any())).thenReturn(stubTimeInfoEmpty());

        assertDoesNotThrow(() -> thingy.doWork(desires));

        fetches4times();
        assertFetchedCourts(Court.getClayIds());
        assertFetchedDate("2023-10-01");

        List<LocalTime> expectedTimes = List.of(
                LocalTime.parse("18:00"),
                LocalTime.parse("18:30"),
                LocalTime.parse("19:00"),
                LocalTime.parse("19:30"));
        List<LocalTime> actualTimes = timeCaptor.getAllValues();
        assertEquals(expectedTimes, actualTimes);

        doesNotFind();
    }

    @Test
    void requestedAny_noPriorOrders_allYouAskIsVacant_searches_1_timeAndFinds() {
        mockOrders(new ArrayList<>());
        List<Desire> desires = stubDesires("2023-10-01", ANY, Court.getClayIds());

        // whatever you ask, there's a booking
        when(fetcher.postTimeInfoBatch(any(), any(), any())).thenAnswer(e -> stubTimeInfo(e.getArgument(1), e.getArgument(2)));

        assertDoesNotThrow(() -> thingy.doWork(desires));

        fetchesOnce();
        assertFetchedCourts(Court.getClayIds());
        assertFetchedDate("2023-10-01");
        assertFetchedTime("18:00");
        finds();
    }








    // todo   maybe also do tests that have earlier, but test asks for LATER -- does not find
    @ParameterizedTest
    @CsvSource({"18:00", "19:30"})
    void requestedAny_canFindBothEarlierOrLater(String newTime) {    // todo make LocalTime
        mockOrders(stubOrders(Court.H02,"2023-10-01", "18:30", "19:30"));
        List<Desire> desires = stubDesires("2023-10-01", ANY, Court.getHardIds());

        // in mocking, last mock matters. So, all are made to be empty, but then, if second mock is more specific, only second one will be in effect.
        when(fetcher.postTimeInfoBatch(any(), any(), any())).thenReturn(stubTimeInfoEmpty());
        when(fetcher.postTimeInfoBatch(List.of(Court.H02.getCourtId()), LocalDate.parse("2023-10-01"), LocalTime.parse(newTime)))
                .thenReturn(stubTimeInfo(Court.H02.getCourtId(), LocalDate.parse("2023-10-01"), LocalTime.parse(newTime), 30L));

        assertDoesNotThrow(() -> thingy.doWork(desires));

//        fetchesOnceOrTwice(); // could be many times because searches for earlier, then later. But could be the other way around and search count can change depending on where the vacancy is
        fetchesAtLeastOnce();
        assertFetchedCourts(List.of(Court.H02.getCourtId()));
        assertFetchedDate("2023-10-01");
        assertFetchedTime(newTime);
        finds();
    }










    @ParameterizedTest
    @CsvSource({"30", "60", "90", "120"})
    void requestedAny_noPriorOrders_findsAnyDurationSlotExcept30min(int minutes) {
        mockOrders(new ArrayList<>());
        List<Desire> desires = stubDesires("2023-10-01", ANY, Court.getClayIds());

        // whatever you ask, there's a booking
        when(fetcher.postTimeInfoBatch(any(), any(), any()))
                .thenReturn(stubTimeInfo(Court.C01.getCourtId(), LocalDate.parse("2023-10-01"), LocalTime.parse("18:00"), minutes));

        assertDoesNotThrow(() -> thingy.doWork(desires));

        if (minutes == 30) {
            // 30 min slot is too short, we don't want it
            doesNotFind();
        } else {
            // 60, 90, 120 are checked
            fetchesOnce();
            assertFetchedCourts(Court.getClayIds());
            assertFetchedDate("2023-10-01");
            assertFetchedTime("18:00");
            finds();
        }
    }




    // adjacent (same court extension)
    @ParameterizedTest
    @EnumSource(value = ExtensionInterest.class, names = {"EARLIER", "ANY"})
    void requestedEarlier_sameCourtHalfHourEarlierExists_findsInSameCourt(ExtensionInterest earlierOrAny) {
        Court h02 = Court.H02;
        String day = "2023-10-01";
        mockOrders(stubOrders(h02, day, "17:30", "19:00"));
        List<Desire> desires = stubDesires(day, earlierOrAny, Court.getClayIds());

        // in mocking, last mock matters. So, all are made to be empty, but then, if second mock is more specific, only second one will be in effect.
        String vacancyAt1700 = "17:00";
        when(fetcher.postTimeInfoBatch(any(), any(), any())).thenReturn(stubTimeInfoEmpty());
        when(fetcher.postTimeInfoBatch(List.of(h02.getCourtId()), LocalDate.parse(day), LocalTime.parse(vacancyAt1700)))
                .thenReturn(stubTimeInfo(h02.getCourtId(), LocalDate.parse(day), LocalTime.parse(vacancyAt1700), 30L));

        assertDoesNotThrow(() -> thingy.doWork(desires));

        fetchesOnce();
        assertFetchedCourts(List.of(h02.getCourtId()));
        assertFetchedDate(day);
        assertFetchedTime(vacancyAt1700);
        finds();
    }

    // adjacent (same court extension)
    @ParameterizedTest
    @EnumSource(value = ExtensionInterest.class, names = {"LATER", "ANY"})
    void requestedLater_sameCourtHalfHourLaterExists_findsInSameCourt(ExtensionInterest laterOrAny) {
        Court h02 = Court.H02;
        String day = "2023-10-01";
        mockOrders(stubOrders(h02, day, "17:30", "19:00"));
        List<Desire> desires = stubDesires(day, laterOrAny, Court.getClayIds());

        // in mocking, last mock matters. So, all are made to be empty, but then, if second mock is more specific, only second one will be in effect.
        String vacancyAt1900 = "19:00";
        when(fetcher.postTimeInfoBatch(any(), any(), any())).thenReturn(stubTimeInfoEmpty());
        when(fetcher.postTimeInfoBatch(List.of(h02.getCourtId()), LocalDate.parse(day), LocalTime.parse(vacancyAt1900)))
                .thenReturn(stubTimeInfo(h02.getCourtId(), LocalDate.parse(day), LocalTime.parse(vacancyAt1900), 30L));

        assertDoesNotThrow(() -> thingy.doWork(desires));

        fetchesAtLeastOnce();
        assertFetchedCourts(List.of(h02.getCourtId()));
        assertFetchedDate(day);
        assertFetchedTime(vacancyAt1900);
        finds();
    }

    // effectively adjacent (different court but no gap between reservation and new vacancy)
    @ParameterizedTest
    @EnumSource(value = ExtensionInterest.class, names = {"EARLIER", "ANY"})
    void requestedEarlier_sameCourtNoVacanciesButYesVacanciesInOtherCourts_findsBrandNew60Min(ExtensionInterest earlierOrAny) {
        Court h02 = Court.H02;
        String day = "2023-10-01";
        mockOrders(stubOrders(h02, day, "17:30", "19:00"));
        List<Desire> desires = stubDesires(day, earlierOrAny, Court.getClayIds());

        List<Long> allCourtsExceptBookedOrder = new ArrayList<>(Court.getClayIds());
        allCourtsExceptBookedOrder.removeIf(e -> Objects.equals(e, h02.getCourtId()));
        LocalDate dateVacancy = LocalDate.parse(day);
        LocalTime timeFromVacancy = LocalTime.parse("17:00");

        // in mocking, last mock matters. So, all are made to be empty, but then, if second mock is more specific, only second one will be in effect.
        when(fetcher.postTimeInfoBatch(any(), any(), any())).thenReturn(stubTimeInfoEmpty());
        when(fetcher.postTimeInfoBatch(allCourtsExceptBookedOrder, dateVacancy, timeFromVacancy)).thenReturn(stubTimeInfo(Court.C01.getCourtId(), dateVacancy, timeFromVacancy, 60L));

        assertDoesNotThrow(() -> thingy.doWork(desires));

        fetchesTwice();
        List<List<Long>> courtsCaptured = courtsCaptor.getAllValues();
        assertEquals(List.of(h02.getCourtId()), courtsCaptured.get(0)); // first looks for extension of booking // todo remove? currently works but irrelevant. implementation could do diff things
        assertEquals(Court.getClayIds(), courtsCaptured.get(1));        // next, looks in all desire's courtIds // todo remove? currently works but irrelevant. implementation could do diff things

        assertFetchedDate(day);
        assertFetchedTime("17:00");
        finds();
    }

    // effectively adjacent (different court but no gap between reservation and new vacancy)
    @ParameterizedTest
    @EnumSource(value = ExtensionInterest.class, names = {"LATER", "ANY"})
    void requestedLater_sameCourtNoVacanciesButYesVacanciesInOtherCourts_findsBrandNew60Min(ExtensionInterest laterOrAny) {
        Court h02 = Court.H02;
        String day = "2023-10-01";
        mockOrders(stubOrders(h02, day, "17:30", "19:00"));

        // fixme. this test asks asks for "later clay" but pre-existing order has H02. Shouldn't it only search for later clay? If you don't care what court, make a desire with all courts.
        List<Desire> desires = stubDesires(day, laterOrAny, Court.getClayIds());

        List<Long> allCourtsExceptBookedOrder = new ArrayList<>(Court.getClayIds());
        allCourtsExceptBookedOrder.removeIf(e -> Objects.equals(e, h02.getCourtId()));
        LocalDate dateVacancy = LocalDate.parse(day);
        LocalTime timeFromVacancy = LocalTime.parse("18:30");

        // in mocking, last mock matters. So, all are made to be empty, but then, if second mock is more specific, only second one will be in effect.
        when(fetcher.postTimeInfoBatch(any(), any(), any())).thenReturn(stubTimeInfoEmpty());
        when(fetcher.postTimeInfoBatch(allCourtsExceptBookedOrder, dateVacancy, timeFromVacancy)).thenReturn(stubTimeInfo(Court.C01.getCourtId(), dateVacancy, timeFromVacancy, 60L));

        assertDoesNotThrow(() -> thingy.doWork(desires));

        fetchesAtLeastOnce();
        List<List<Long>> courtsCaptured = courtsCaptor.getAllValues();
        assertEquals(List.of(h02.getCourtId()), courtsCaptured.get(0));       // first looks for extension of booking
        assertEquals(Court.getClayIds(), courtsCaptured.get(1));                    // next, looks in all desire's courtIds
        // todo. above checks for Court.getClayIds() why? Because desires are for clay ids? Maybe add separate test that verifies that if clay desire --> searches only clay, etc. Include "all indoor courts"

        assertFetchedDate(day);
        assertFetchedTime("18:30");
        finds();
    }

    // non-adjacent (there's a gap between reservation and new vacancy)
    @ParameterizedTest
    @EnumSource(value = ExtensionInterest.class, names = {"EARLIER", "ANY"})
    void requestedEarlier_oneVacancyInNonAdjacentEarlierTime_finds(ExtensionInterest earlierOrAny) {
        String day = "2023-10-01";
        mockOrders(stubOrders(Court.H02, day, "19:30", "20:30"));

        List<Desire> desires = stubDesires(day, earlierOrAny, Court.getClayIds());

        List<Long> allCourts = new ArrayList<>(Court.getClayIds());
        LocalDate dateVacancy = LocalDate.parse(day);
        LocalTime timeFromVacancy = LocalTime.parse("18:00");
        // in mocking, last mock matters. So, all are made to be empty, but then, if second mock is more specific, only second one will be in effect.
        when(fetcher.postTimeInfoBatch(any(), any(), any())).thenReturn(stubTimeInfoEmpty());
        when(fetcher.postTimeInfoBatch(allCourts, dateVacancy, timeFromVacancy)).thenReturn(stubTimeInfo(Court.C01.getCourtId(), dateVacancy, timeFromVacancy, 60L));

        assertDoesNotThrow(() -> thingy.doWork(desires));

        fetches4times();
        List<LocalTime> timesCaptured = timeCaptor.getAllValues();
        // todo will probably decommission these, because it's implementation details, we shouldn't care. Unless for performance reasons if it searches for way too many times for some reason
        assertEquals(LocalTime.parse("19:00"), timesCaptured.get(0));   // first looks for extension of booking  (same H02 court 19:00-19:30 -- for half hour)
        assertEquals(LocalTime.parse("19:00"), timesCaptured.get(1));   // first looks for brand-new reservation (clay court     19:00-20:00 -- for one hour)
        assertEquals(LocalTime.parse("18:30"), timesCaptured.get(2));   // next, looks for brand-new reservation (clay court     18:30-19:30 -- for one hour)
        assertEquals(LocalTime.parse("18:00"), timesCaptured.get(3));   // next, looks for brand-new reservation (clay court     18:00-19:00 -- for one hour)

        assertFetchedDate(day);
        assertFetchedTime("18:00");
        finds();
    }

    // non-adjacent (there's a gap between reservation and new vacancy)
    @ParameterizedTest
    @EnumSource(value = ExtensionInterest.class, names = {"LATER", "ANY"})
    void requestedLater_oneVacancyInNonAdjacentLaterTime_finds(ExtensionInterest laterOrAny) {
        String day = "2023-10-01";
        mockOrders(stubOrders(Court.H02, day, "17:30", "19:00"));

        List<Desire> desires = stubDesires(day, laterOrAny, Court.getClayIds());

        List<Long> allCourts = new ArrayList<>(Court.getClayIds());
        LocalDate dateVacancy = LocalDate.parse(day);
        LocalTime timeFromVacancy = LocalTime.parse("19:30");
        // in mocking, last mock matters. So, all are made to be empty, but then, if second mock is more specific, only second one will be in effect.
        when(fetcher.postTimeInfoBatch(any(), any(), any())).thenReturn(stubTimeInfoEmpty());
        when(fetcher.postTimeInfoBatch(allCourts, dateVacancy, timeFromVacancy)).thenReturn(stubTimeInfo(Court.C01.getCourtId(), dateVacancy, timeFromVacancy, 60L));

        assertDoesNotThrow(() -> thingy.doWork(desires));

        fetchesAtLeastOnce();
        assertFetchedDate(day);
        assertFetchedTime("19:30");
        finds();
    }

    private void finds() {
        verify(audioPlayer).chimeIfNecessary();
    }

    private void doesNotFind() {
        verify(audioPlayer, never()).chimeIfNecessary();
    }

    private void mockOrders(List<Order> orders) {
        orderConverterMockedStatic.when(() -> SebOrderConverter.toOrders(any())).thenReturn(orders);
    }

    private static List<Desire> stubDesires(String date, ExtensionInterest extensionInterest, List<Long> courtIds) {
        List<Desire> desires = new ArrayList<>();
        desires.add(new Desire(LocalDate.parse(date), extensionInterest, courtIds));
        return desires;
    }

    private void fetchesAtLeastOnce() {
        verify(fetcher, atLeastOnce()).postTimeInfoBatch(courtsCaptor.capture(), dateCaptor.capture(), timeCaptor.capture());
    }

    private void fetchesOnce() {
        verify(fetcher).postTimeInfoBatch(courtsCaptor.capture(), dateCaptor.capture(), timeCaptor.capture());
    }

    private void fetchesOnceOrTwice() {
        verify(fetcher, atLeast(1)).postTimeInfoBatch(courtsCaptor.capture(), dateCaptor.capture(), timeCaptor.capture());
        verify(fetcher, atMost(2)).postTimeInfoBatch(courtsCaptor.capture(), dateCaptor.capture(), timeCaptor.capture());
    }

    private void fetchesTwice() {
        verify(fetcher, times(2)).postTimeInfoBatch(courtsCaptor.capture(), dateCaptor.capture(), timeCaptor.capture());
    }

    private void fetches4times() {
        verify(fetcher, times(4)).postTimeInfoBatch(courtsCaptor.capture(), dateCaptor.capture(), timeCaptor.capture());
    }

    private void assertFetchedCourts(List<Long> courtIds) {
        assertEquals(courtIds, courtsCaptor.getValue());
    }

    private void assertFetchedDate(String date) {
        assertEquals(LocalDate.parse(date), dateCaptor.getValue());
    }

    private void assertFetchedTime(String time) {
        assertEquals(LocalTime.parse(time), timeCaptor.getValue());
    }
}