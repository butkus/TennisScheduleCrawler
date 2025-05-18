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

import static com.butkus.tenniscrawler.ExtensionInterest.ANY;
import static com.butkus.tenniscrawler.ExtensionInterest.NONE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DesiresIteratorThingyTest {

    public static final String DAY = "2023-10-01";

    @Mock private AudioPlayer audioPlayer;
    @Mock private SebFetcher fetcher;
    private DesiresIteratorThingy thingy;

//    @InjectMocks private DesiresIteratorThingy thingy;
    private MockedStatic<SebOrderConverter> orderConverterMockedStatic;

    @Captor private ArgumentCaptor<List<Long>> courtsCaptor;
    @Captor private ArgumentCaptor<LocalDate> dateCaptor;
    @Captor private ArgumentCaptor<LocalTime> timeCaptor;

    @BeforeEach
    void setUp() {
        orderConverterMockedStatic = mockStatic(SebOrderConverter.class);
        BookingConfigurator configurator = new BookingConfigurator(audioPlayer, fetcher);
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

    private List<Order> stubOrders(Court court, String timeFrom, long duration) {
        String timeTo = LocalTime.parse(timeFrom).plusMinutes(duration).toString();
        return stubOrders(court, DAY, timeFrom, timeTo);
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





    // TODO -- 1 -- do not check at all when optimization is in place,
    //  i.e. postPlaceInfoBatch will determine no free slots and no need for postTimeInfoBatch to search
    // TODO -- 2 -- reading this test after a long time, it is:
    //      - not so easily readable (visually distinct blocks would be nice, e.g. precondition, precondition, method call, assertions)
    //      - after familiarizing, the test looks quite succinct, and readable
    //      - would be nice to somehow organize the tests better, maybe more nesting, maybe more classes.
    //      -   so that test code is first class citizen as prod code is (TDD growing pains)
    @Test
    void requestedAny_noPriorOrders_nothingExists_searches_4_timesAndDoesNotFind() {

        mockOrders(new ArrayList<>());

        List<Desire> desires = stubDesires(DAY, ANY, Court.getClayIds());

        // whatever you ask, there's NO booking
        when(fetcher.postTimeInfoBatch(any(), any(), any())).thenReturn(stubTimeInfoEmpty());

        assertDoesNotThrow(() -> thingy.doWork(desires));

        fetches4times();
        assertFetchedCourts(Court.getClayIds());
        assertFetchedDate(DAY);

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
        List<Desire> desires = stubDesires(DAY, ANY, Court.getClayIds());

        // whatever you ask, there's a booking
        when(fetcher.postTimeInfoBatch(any(), any(), any())).thenAnswer(e -> stubTimeInfo(e.getArgument(1), e.getArgument(2)));

        assertDoesNotThrow(() -> thingy.doWork(desires));

        fetchesOnce();
        assertFetchedCourts(Court.getClayIds());
        assertFetchedDate(DAY);
        assertFetchedTime("18:00");
        finds();
    }






    // todo   move to nested "extension interest" section?
    // todo   maybe also do tests that have earlier, but test asks for LATER -- does not find
    @ParameterizedTest
    @CsvSource({"18:00", "19:30"})
    void requestedAny_canFindBothEarlierOrLater(String newTime) {    // todo make LocalTime
        mockOrders(stubOrders(Court.H02, DAY, "18:30", "19:30"));       // todo: orders? or reservations?
        List<Desire> desires = stubDesires(DAY, ANY, Court.getHardIds());

        Court h02 = Court.H02;
        stubEmptyExcept(List.of(h02.getCourtId()), h02, LocalTime.parse(newTime), 30L);

        assertDoesNotThrow(() -> thingy.doWork(desires));

//        fetchesOnceOrTwice(); // could be many times because searches for earlier, then later. But could be the other way around and search count can change depending on where the vacancy is
        fetchesAtLeastOnce();
        assertFetchedCourts(List.of(Court.H02.getCourtId()));
        assertFetchedDate(DAY);
        assertFetchedTime(newTime);
        finds();
    }


// todo leave this test in here or move to DesireOrderPairer? handling logic is in there
    @ParameterizedTest
    @EnumSource(value = ExtensionInterest.class, names = {"EARLIER", "LATER"})
    void requestedEarlierOrLater_dontHaveOrders_throws(ExtensionInterest interest) {
        List<Desire> desires = stubDesires(DAY, interest, Court.getHardIds());
        assertThrows(EarlierOrLaterDesireMustHaveOwnOrderException.class, () -> thingy.doWork(desires));
    }


    @ParameterizedTest
    @CsvSource({"30", "60", "90", "120"})
    void requestedAny_noPriorOrders_findsAnyDurationSlotExcept30min(int minutes) {
        mockOrders(new ArrayList<>());
        List<Desire> desires = stubDesires(DAY, ANY, Court.getClayIds());

        // whatever you ask, there's a booking
        when(fetcher.postTimeInfoBatch(any(), any(), any()))
                .thenReturn(stubTimeInfo(Court.C01.getCourtId(), LocalDate.parse(DAY), LocalTime.parse("18:00"), minutes));

        assertDoesNotThrow(() -> thingy.doWork(desires));

        if (minutes == 30) {
            // 30 min slot is too short, we don't want it
            doesNotFind();
        } else {
            // 60, 90, 120 are checked
            fetchesOnce();
            assertFetchedCourts(Court.getClayIds());
            assertFetchedDate(DAY);
            assertFetchedTime("18:00");
            finds();
        }
    }


    // adjacent (same court extension)
    @ParameterizedTest
    @EnumSource(value = ExtensionInterest.class, names = {"EARLIER", "ANY"})
    void requestedEarlier_sameCourtHalfHourEarlierExists_findsInSameCourt(ExtensionInterest earlierOrAny) {
        Court h02 = Court.H02;
        mockOrders(stubOrders(h02, DAY, "17:30", "19:00"));
        List<Desire> desires = stubDesires(DAY, earlierOrAny, Court.getHardIds());

        String vacancyAt1700 = "17:00";
        stubEmptyExcept(List.of(h02.getCourtId()), h02, LocalTime.parse(vacancyAt1700), 30L);

        assertDoesNotThrow(() -> thingy.doWork(desires));

        fetchesOnce();
        assertFetchedCourts(List.of(h02.getCourtId()));
        assertFetchedDate(DAY);
        assertFetchedTime(vacancyAt1700);
        finds();
    }

    // adjacent (same court extension)
    @ParameterizedTest
    @EnumSource(value = ExtensionInterest.class, names = {"LATER", "ANY"})
    void requestedLater_sameCourtHalfHourLaterExists_findsInSameCourt(ExtensionInterest laterOrAny) {
        Court h02 = Court.H02;
        mockOrders(stubOrders(h02, DAY, "17:30", "19:00"));
        List<Desire> desires = stubDesires(DAY, laterOrAny, Court.getHardIds());

        String vacancyAt1900 = "19:00";
        stubEmptyExcept(List.of(h02.getCourtId()), h02, LocalTime.parse(vacancyAt1900), 30L);

        assertDoesNotThrow(() -> thingy.doWork(desires));

        fetchesAtLeastOnce();
        assertFetchedCourts(List.of(h02.getCourtId()));
        assertFetchedDate(DAY);
        assertFetchedTime(vacancyAt1900);
        finds();
    }

    // todo marker comment: test with 2 desires
    // todo marker comment: 2 tests above covered. No extra needed because:
    //   - ANY covered
    //   - EARLIER or LATER inapplicable because with no order can't ask for that (will get 'no order for EARLIER/LATER')
    @Test
    void requestedAnyOutsideAndNoneInside_have1insideOrder_insideDoesNotSearch_outsideDoesSearch() {
        mockOrders(stubOrders(Court.H02, DAY, "17:30", "19:00"));
        List<Desire> desires = stubDesires(
                new Desire(LocalDate.parse(DAY), NONE, Court.getIndoorIds()),
                new Desire(LocalDate.parse(DAY), ANY, Court.getOutdoorIds())
        );

        when(fetcher.postTimeInfoBatch(any(), any(), any())).thenReturn(stubTimeInfoEmpty());

        assertDoesNotThrow(() -> thingy.doWork(desires));
        fetches4times();    // does not search for indoors because NONE; searches for brand-new outdoor because ANY
        assertFetchedCourts(Court.getOutdoorIds());
        doesNotFind();
    }








//////// START OF  === PRESERVE-BOOKING-LENGTH CHANGE ===  ///////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////


    // TODO -- MAKE A NESTED CLASS FOR "EFFECTIVELY ADJACENT" AND "NON-ADJACENT"? OR PERHAPS JUST "EFFECTIVELY ADJACENT? --
    //  because "effectively adjacent" has near-full overlap of set-up code.
    //  "non-adjacent" can be not-in-that-class, but there's some methods that apply, too. Make them global methods perhaps

    // effectively adjacent (different court but no gap between reservation and new vacancy)
    @ParameterizedTest
    @CsvSource({
            "EARLIER, 60, 60, true",
            "EARLIER, 60, 90, true",
            "EARLIER, 60, 120, true",
            "EARLIER, 90, 60, false",
            "EARLIER, 90, 90, true",
            "EARLIER, 90, 120, true",
            "EARLIER, 120, 60, false",
            "EARLIER, 120, 90, false",
            "EARLIER, 120, 120, true",
            "ANY, 60, 60, true",
            "ANY, 60, 90, true",
            "ANY, 60, 120, true",
            "ANY, 90, 60, false",
            "ANY, 90, 90, true",
            "ANY, 90, 120, true",
            "ANY, 120, 60, false",
            "ANY, 120, 90, false",
            "ANY, 120, 120, true",
    })
    void requestedEarlierOrAny_sameCourtNoVacanciesButYesVacanciesInOtherCourts_findsBrandNewSameLengthOrLonger(
            ExtensionInterest interest, long orderDuration, long vacancyDuration, boolean shouldFind) {
    // todo: csv source is the same as for 'non-adjacent' counterpart. Extract as @MethodSource?
        String timeFrom = "17:30";
        String searchFrom = LocalTime.parse(timeFrom).minusMinutes(30).toString();
        searchEffectivelyAdjacent(interest, orderDuration, vacancyDuration, shouldFind, timeFrom, searchFrom);
    }

    // effectively adjacent (different court but no gap between reservation and new vacancy)
    @ParameterizedTest
    @CsvSource({
            "LATER, 60, 90, true",
            "LATER, 60, 60, true",
            "LATER, 60, 120, true",
            "LATER, 90, 60, false",
            "LATER, 90, 90, true",
            "LATER, 90, 120, true",
            "LATER, 120, 60, false",
            "LATER, 120, 90, false",
            "LATER, 120, 120, true",
            "ANY, 60, 60, true",
            "ANY, 60, 90, true",
            "ANY, 60, 120, true",
            "ANY, 90, 60, false",
            "ANY, 90, 90, true",
            "ANY, 90, 120, true",
            "ANY, 120, 60, false",
            "ANY, 120, 90, false",
            "ANY, 120, 120, true",
    })
    void requestedLaterOrAny_sameCourtNoVacanciesButYesVacanciesInOtherCourts_findsBrandNewSameLengthOrLonger(
            ExtensionInterest interest, long orderDuration, long vacancyDuration, boolean shouldFind) {
        String timeFrom = "17:30";
        String searchFrom = LocalTime.parse(timeFrom).plusMinutes(orderDuration).minusMinutes(30).toString();
        searchEffectivelyAdjacent(interest, orderDuration, vacancyDuration, shouldFind, timeFrom, searchFrom);
    }

    private void searchEffectivelyAdjacent(ExtensionInterest interest, long orderDuration, long vacancyDuration, boolean shouldFind,
                                           String timeFrom, String searchFrom) {
        // todo param "timeFrom" -- maybe we don't need it? Does it make sense to have flexibility? currently 17:30 for both method calls

        Court h02 = Court.H02;

        mockOrders(stubOrders(h02, timeFrom, orderDuration));
        List<Desire> desires = stubDesires(DAY, interest, Court.getHardIds());

        LocalTime timeFromVacancy = LocalTime.parse(searchFrom);
        stubEmptyExcept(Court.getHardIds(), Court.H01, timeFromVacancy, vacancyDuration);

        assertDoesNotThrow(() -> thingy.doWork(desires));

        if (shouldFind) {
            fetchesAtLeastOnce();
            List<List<Long>> courtsCaptured = courtsCaptor.getAllValues();
            assertEquals(List.of(h02.getCourtId()), courtsCaptured.get(0)); // first looks for extension of booking // todo remove? currently works but irrelevant. implementation could do diff things
            assertEquals(Court.getHardIds(), courtsCaptured.get(1));        // next, looks in all desire's courtIds // todo remove? currently works but irrelevant. implementation could do diff things

            assertFetchedDate(DAY);
            assertFetchedTime(searchFrom);
            finds();
        } else {
            doesNotFind();
        }
    }


    // non-adjacent (there's a gap between reservation and new vacancy)
    @ParameterizedTest
    @CsvSource({
            "EARLIER, 60, 60, true",
            "EARLIER, 60, 90, true",
            "EARLIER, 60, 120, true",
            "EARLIER, 90, 60, false",
            "EARLIER, 90, 90, true",
            "EARLIER, 90, 120, true",
            "EARLIER, 120, 60, false",
            "EARLIER, 120, 90, false",
            "EARLIER, 120, 120, true",
            "ANY, 60, 60, true",
            "ANY, 60, 90, true",
            "ANY, 60, 120, true",
            "ANY, 90, 60, false",
            "ANY, 90, 90, true",
            "ANY, 90, 120, true",
            "ANY, 120, 60, false",
            "ANY, 120, 90, false",
            "ANY, 120, 120, true",
    })
    void requestedEarlierOrAny_oneVacancyInNonAdjacentEarlierTime_finds(
            ExtensionInterest interest, long orderDuration, long vacancyDuration, boolean shouldFind) {

        BookingConfigurator configurator = new BookingConfigurator(audioPlayer, fetcher);
        configurator.setEarlyBird(LocalTime.parse("17:00"));
        thingy = new DesiresIteratorThingy(configurator);

        String timeFrom = "19:30";      // = orderFrom
        String searchFrom = "17:00";    // 17:00 will always have a gap. e.g. even if 120 min, it will be 19:00 which is 30 min before timeFrom = 19:30    todo was 18:00. Might need EarlyBird adjustment (hopefully that's it)
        searchNonAdjacent(timeFrom, interest, orderDuration, vacancyDuration, shouldFind, searchFrom);
    }

    // non-adjacent (there's a gap between reservation and new vacancy)
    @ParameterizedTest
    @CsvSource({
            "LATER, 60, 90, true",
            "LATER, 60, 60, true",
            "LATER, 60, 120, true",
            "LATER, 90, 60, false",
            "LATER, 90, 90, true",
            "LATER, 90, 120, true",
            "LATER, 120, 60, false",
            "LATER, 120, 90, false",
            "LATER, 120, 120, true",
            "ANY, 60, 60, true",
            "ANY, 60, 90, true",
            "ANY, 60, 120, true",
            "ANY, 90, 60, false",
            "ANY, 90, 90, true",
            "ANY, 90, 120, true",
            "ANY, 120, 60, false",
            "ANY, 120, 90, false",
            "ANY, 120, 120, true",
    })
    void requestedLaterOrAny_oneVacancyInNonAdjacentLaterTime_finds(
            ExtensionInterest interest, long orderDuration, long vacancyDuration, boolean shouldFind) {
        BookingConfigurator configurator = new BookingConfigurator(audioPlayer, fetcher);
        configurator.setEarlyBird(LocalTime.parse("17:00"));
        thingy = new DesiresIteratorThingy(configurator);

        String timeFrom = "17:00";  // = orderFrom
        String searchFrom = "19:30";    // 19:30 will always have a gap. e.g. even if 120 min, end of vacancy will be 19:00 which is 30 min before 19:30
        searchNonAdjacent(timeFrom, interest, orderDuration, vacancyDuration, shouldFind, searchFrom);
    }

    private void searchNonAdjacent(String orderFrom, ExtensionInterest interest, long orderDuration, long vacancyDuration, boolean shouldFind, String searchFrom) {
        mockOrders(stubOrders(Court.H02, orderFrom, orderDuration));

        List<Desire> desires = stubDesires(DAY, interest, Court.getNonSquashIds());

        List<Long> allCourts = new ArrayList<>(Court.getNonSquashIds());
        LocalTime timeFromVacancy = LocalTime.parse(searchFrom);
        stubEmptyExcept(allCourts, Court.H02, timeFromVacancy, vacancyDuration);

        assertDoesNotThrow(() -> thingy.doWork(desires));

        if (shouldFind) {
            fetchesAtLeastOnce();
            assertFetchedDate(DAY);
            assertFetchedTime(searchFrom);
            finds();
        } else {
            doesNotFind();
        }
    }


//////// END OF  === PRESERVE-BOOKING-LENGTH CHANGE ===  ///////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////








//////// START OF  === DOUBLE-BOOKING (indoors + outdoors) ===  ///////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////


    // 1. fix existing tests -- should respect desires. If clayId's --> only finds clay -- done
    // 2. add new tests or amend old ones -- multiple desires a day f-nality
    //    there's 1 already with "test with 2 desires" comment


//////// END OF  === DOUBLE-BOOKING (indoors + outdoors) ===  ///////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////








    // todo "empty" is ambiguous;  can be NO AVAILABILITY (empty available court list) or YES AVAILABILITY (all courts you want are not resererved <--> empty)
    //   name idea: stubNoVacanciesExcept?
    private void stubEmptyExcept(List<Long> requestedCourts, Court returnedCourt, LocalTime time, long vacancyDuration) {
        LocalDate day = LocalDate.parse(DAY);
        // in mocking, last mock matters. So, all are made to be empty, but then, if second mock is more specific, only second one will be in effect.
        when(fetcher.postTimeInfoBatch(any(), any(), any())).thenReturn(stubTimeInfoEmpty());

        // todo: add validation? requestedCourts and returnedCourt.getCourtId() should be from the same pool, e.g. indoorCourts, H01 or outdoorCourts, G01
        when(fetcher.postTimeInfoBatch(requestedCourts, day, time))
                .thenReturn(stubTimeInfo(returnedCourt.getCourtId(), day, time, vacancyDuration));
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

    private static List<Desire> stubDesires(Desire... desires) {
        return new ArrayList<>(List.of(desires));
    }

    // todo remove redundant desire infra
    private static List<Desire> stubDesires(String date, ExtensionInterest extensionInterest, List<Long> courtIds, List<Long> alternativeCourtIds) {
        List<Desire> desires = new ArrayList<>();
        desires.add(new Desire(LocalDate.parse(date), extensionInterest, courtIds, alternativeCourtIds));
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