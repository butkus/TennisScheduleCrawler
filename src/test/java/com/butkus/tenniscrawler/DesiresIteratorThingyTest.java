package com.butkus.tenniscrawler;

import com.butkus.tenniscrawler.rest.SebFetcher;
import com.butkus.tenniscrawler.rest.SebOrderConverter;
import com.butkus.tenniscrawler.rest.orders.Order;
import com.butkus.tenniscrawler.rest.placeinfobatch.DataInner;
import com.butkus.tenniscrawler.rest.placeinfobatch.PlaceInfoBatchRspDto;
import com.butkus.tenniscrawler.rest.timeinfobatch.AviableDuration;
import com.butkus.tenniscrawler.rest.timeinfobatch.DataTimeInfo;
import com.butkus.tenniscrawler.rest.timeinfobatch.TimeInfoBatchRspDto;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.butkus.tenniscrawler.ExtensionInterest.ANY;
import static com.butkus.tenniscrawler.ExtensionInterest.NONE;
import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DesiresIteratorThingyTest {

    public static final String DAY = "2023-10-01";

    public static final String DAY_1 = "2023-10-01";
    public static final String DAY_2 = "2023-10-02";
    public static final String DAY_3 = "2023-10-03";
    public static final String DAY_4 = "2023-10-04";
    public static final String DAY_8 = "2023-10-08";
    public static final String DAY_9 = "2023-10-09";
    public static final String DAY_11 = "2023-10-11";
    public static final String DAY_12 = "2023-10-12";
    public static final String DAY_16 = "2023-10-16";
    public static final String DAY_17 = "2023-10-17";
    public static final String DAY_18 = "2023-10-18";
    public static final String DAY_19 = "2023-10-19";
    public static final String DAY_26 = "2023-10-26";
    public static final String DAY_27 = "2023-10-27";

    public static final String RECIPE_BASED = "recipe-based";

    public static final LocalTime T1800 = LocalTime.parse("18:00");
    public static final LocalTime T1830 = LocalTime.parse("18:30");
    public static final LocalTime T1900 = LocalTime.parse("19:00");
    public static final LocalTime T1930 = LocalTime.parse("19:30");
    public static final LocalTime T2000 = LocalTime.parse("20:00");
    public static final LocalTime T2030 = LocalTime.parse("20:30");
    public static final LocalTime T2100 = LocalTime.parse("21:00");
    public static final LocalTime T2130 = LocalTime.parse("21:30");

    public static final List<Integer> GRASS_AND_CLAY_TYPES = List.of(CourtType.GRASS.getCourtTypeId(), CourtType.CLAY.getCourtTypeId());
    public static final List<Integer> HARD_AND_CARPET_TYPES = List.of(CourtType.HARD.getCourtTypeId(), CourtType.HARD_2.getCourtTypeId(), CourtType.CARPET.getCourtTypeId());
    public static final List<Integer> HARD_OLD_ONLY_AND_CARPET_TYPES = List.of(CourtType.HARD.getCourtTypeId(), CourtType.CARPET.getCourtTypeId());

    @Mock private AudioPlayer audioPlayer;
    @Mock private SebFetcher fetcher;
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2023-10-01T12:00:00Z"), UTC);

    private DesiresIteratorThingy thingy;
    //    @InjectMocks private DesiresIteratorThingy thingy;


    private MockedStatic<SebOrderConverter> orderConverterMockedStatic;

    @Captor
    private ArgumentCaptor<List<Long>> courtsCaptor;
    @Captor
    private ArgumentCaptor<LocalDate> dateCaptor;
    @Captor
    private ArgumentCaptor<LocalTime> timeCaptor;

    @Captor
    private ArgumentCaptor<List<String>> datesCaptor;
    @Captor
    private ArgumentCaptor<List<Integer>> placesCaptor;

    @Captor
    private ArgumentCaptor<VacancyFound> vacancyFoundCaptor;

    @Captor
    private ArgumentCaptor<List<DataInner>> courtDtosCaptor;

    @BeforeEach
    void setUp() throws Exception {
        orderConverterMockedStatic = mockStatic(SebOrderConverter.class);
        BookingConfigurator configurator = new BookingConfigurator(audioPlayer, fetcher, CLOCK);
        thingy = spy(new DesiresIteratorThingy(configurator));
        when(fetcher.postPlaceInfoBatch(any(), any())).thenReturn(SebStubs.stubPlaceInfoFull());
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

    private static List<Order> stubOrders(Order... orders) {
        return new ArrayList<>(List.of(orders));
    }

    private List<Order> stubOrders(Court court, String timeFrom, long duration) {
        String timeTo = LocalTime.parse(timeFrom).plusMinutes(duration).toString();
        return stubOrders(court, DAY, timeFrom, timeTo);
    }

    private static List<Order> stubOrders(String date, String timeFrom, String timeTo) {
        return stubOrders(Court.H01, date, timeFrom, timeTo);
    }

    private static TimeInfoBatchRspDto stubTimeInfoOccupied() {
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

// todo (after finished with postPlaceInfoBatch):
//  === ITERATOR THINGY SHOULD DO
//       - fetching orders
//       - preparing desires
//       - pairing desires
//       - fetching postPlaceInfoBatch
//       - iterating desires and finding vacancies
//  === IT SHOULD NOT DO
//       - test vacancy logic (it currently does)
//       - most of the old tests should be moved to VacancyTest or whatever and tested one-by-one
//       - currently it's halfway to being an integration test
// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//   postPlaceInfoBatch tests  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // todo: all orders fit in 1 day --> fetches once
    // todo: all orders fit in 8 days --> fetches once
    // todo: all orders fit in 9 days --> feches 2 times
    // todo: all orders fit in 16 days --> fetches 2 times
    // todo: all orders fit in 17 days --> fetches 3 times
    // todo  ALL ABOVE DONE

    @Nested
    class FetchCount {

        @BeforeEach
        void setUp() throws Exception {
            when(fetcher.postPlaceInfoBatch(any(), any())).thenReturn(SebStubs.stubPlaceInfo7Days());
        }

        @Test
        void desiresSpan1day_fetchesOnce() {
            thingy.doWork(stubDesireForDays(DAY_11));
            fetchesOncePlaces();
        }

        @Test
        void desiresSpan8days_fetchesOnce() {
            thingy.doWork(stubDesireForDays(DAY_11, DAY_18));
            fetchesOncePlaces();
        }

        @Test
        void desiresSpan9days_fetches2times() {
            thingy.doWork(stubDesireForDays(DAY_11, DAY_19));
            fetchesPlaces(2);
        }

        @Test
        void desiresSpan16days_fetches2times() {
            thingy.doWork(stubDesireForDays(DAY_11, DAY_26));
            fetchesPlaces(2);
        }

        @Test
        void desiresSpan17days_fetches3times() {
            thingy.doWork(stubDesireForDays(DAY_11, DAY_27));
            fetchesPlaces(3);
        }

        public List<Desire> stubDesireForDays(String... dates) {
            List<Desire> desires = new ArrayList<>();
            for (String date : dates) {
                desires.add(new Desire(LocalDate.parse(date), new OutdoorOnlyRecipe()));
            }
            return desires;
        }


        @Test
        void desiresFor2days_passes1daysDtosToVacancyScanner() {
            // whatever you ask, there's NO booking
            when(fetcher.postTimeInfoBatch(any(), any(), any())).thenReturn(stubTimeInfoOccupied());

            thingy.doWork(stubDesireForDays(DAY_1, DAY_2));
            assertCourtDtosFilteredFor1day();
            // todo check that that 1 day is today
        }
    }


    // todo maybe group with some other tests. This verifies if post__TIME___InfoBatch is called for 48-hour desires
    @Tag(RECIPE_BASED)
    @Test
    void desireToday_WhenPlaceInfoFindsVacancy_ThenTimeInfoCalled() throws Exception {
        mockOrders(new ArrayList<>());

        // todo DAY_2 and DAY_3 also -- because tomorrow is always within 48 hours; day 3 may or may not be within 48 hours and for orders we have we can know if they are in- or out- 48-hours, but for timeInfoBatch we cannot pass time parameter, only date, therefore, we assume that day 3 is also volatile (and DAY4 does not do timeInfoBActch -- another test)
        //   OR don't check days, but mock volatile true (with this test) and false (with another test) --> less integration-test-y to only check 1 module (VacancyTest already checks which days are volatile)
        List<Desire> desires = Stubs.stubDesiresRecipe(DAY, new IndoorSimple());

        // fullsell at 19:30, 20:00, and 21:30 slots.
        // They might be sellable in 30-min slots, but maybe in 30+60/60+30 or maybe 90-min-only, we don't know yet. placeInfoBatch does not tell us that
        when(fetcher.postPlaceInfoBatch(any(), any())).thenReturn(SebStubs.stubPlaceInfoHard01at1930fullsell90Mins());

        // timeInfoBach does tell if aviable duration is 90-min only, or 30+60, or 60+30, or 30+30+30
        // todo: this stubs 20:00-21:00 unequivocally free (60-min-only slot). Need to cover other options too (or make it clear that they are not relevant to cover)
        // todo: this is a bit false because 19:30-20:00 is not accounted for
        stubOccupiedExcept(List.of(Court.H01.getCourtId()), Court.H01, LocalTime.parse("20:00"), 60L);

        assertDoesNotThrow(() -> thingy.doWork(desires));

        fetchesOncePlaces();    // placeInfoBatch (todo rename. maybe fetchesPlaceOnce()?

        fetchesTwice();         // timeInfoBatch  (todo rename. maybe fetchesTimeTwice()?
        // based on placeInfoBatch response (fullsell 19:30-21:00)
        //   - checks weight 1 (HARD, 19:30), calls timeInfoBatch(HARD, 19:30, 60 mins), does not find. Then,
        //   - does not check weight 2 because (fullsell 19:30-21:00 hard) says there's nothing for it. Then,
        //   - checks weight 3
        //          - does not check first in line (HARD, 18:30)
        //          - checks second in line -- (HARD 20:00) and finds.
        assertThat(timeCaptor.getAllValues()).containsExactly(T1930, T2000);
        assertThat(dateCaptor.getAllValues()).containsExactly(LocalDate.parse(DAY), LocalDate.parse(DAY));
        assertThat(courtsCaptor.getAllValues()).containsExactly(List.of(1L), List.of(1L));

        assertVacancyFound(new VacancyFound(Court.H01.getCourtId(), LocalDate.parse(DAY), T2000, T2100));
    }

//   postPlaceInfoBatch tests  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


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

        List<Desire> desires = Stubs.stubDesires(DAY, ANY, Court.getClayIds());

        // whatever you ask, there's NO booking
        when(fetcher.postTimeInfoBatch(any(), any(), any())).thenReturn(stubTimeInfoOccupied());

        assertDoesNotThrow(() -> thingy.doWork(desires));

        fetches4times();
        assertFetchedCourts(Court.getClayIds());
        assertFetchedDate(DAY);

        List<LocalTime> expectedTimes = List.of(
                LocalTime.parse("18:00"),
                LocalTime.parse("18:30"),
                T1900,
                LocalTime.parse("19:30"));
        List<LocalTime> actualTimes = timeCaptor.getAllValues();
        assertEquals(expectedTimes, actualTimes);

        doesNotFind();
    }


    @Tag(RECIPE_BASED)
    @Test
    void noPriorOrders_OutdoorRecipe_nothingExists_doesNotFind() throws Exception {
        mockOrders(new ArrayList<>());
        List<Desire> desires = Stubs.stubDesiresRecipe(DAY, new OutdoorOnlyRecipe());
        when(fetcher.postPlaceInfoBatch(any(), any())).thenReturn(SebStubs.stubPlaceInfoFull());

        assertDoesNotThrow(() -> thingy.doWork(desires));

        fetchesOncePlaces();
        assertVacancyNotFound();
    }

    @Tag(RECIPE_BASED)
    @Test
    void noPriorOrders_IndoorRecipe_nothingExists_doesNotFind() throws Exception {
        mockOrders(new ArrayList<>());

        List<Desire> desires = Stubs.stubDesiresRecipe(DAY, new IndoorSimple());

        // wrong stub (Outdoor vacancy) -- should result in "nothing found"
        when(fetcher.postPlaceInfoBatch(any(), any())).thenReturn(SebStubs.stubPlaceInfoFull());

        assertDoesNotThrow(() -> thingy.doWork(desires));

        fetchesOncePlaces();

        assertVacancyNotFound();
    }


    @Tag(RECIPE_BASED)
    @Test
    void noPriorOrders_bestCourtIsFree_finds() throws Exception {
        mockOrders(new ArrayList<>());
        String day = DAY_11;
        List<Desire> desires = Stubs.stubDesiresRecipe(day, new OutdoorOnlyRecipe());
        when(fetcher.postPlaceInfoBatch(any(), any())).thenReturn(SebStubs.stubPlaceInfoClay01at1900free());

        assertDoesNotThrow(() -> thingy.doWork(desires));

        assertVacancyFound(new VacancyFound(Court.C01.getCourtId(), LocalDate.parse(day), T1900, T2000));
    }

    @Nested
    class NoPriorOrders_thirdBestCourtIsFree_finds {

//    Third best is:
//    weight 4
//    List.of(
//            new CourtTypeAtHour(CourtTypeCustom.CLAY_SUMMER, T1830),
//            new CourtTypeAtHour(CourtTypeCustom.CLAY_REST, T1900)

        @Tag(RECIPE_BASED)
        @Test
        void firstOption_claySummerAt1830() throws Exception {
            mockOrders(new ArrayList<>());

            String day = DAY_11;
            List<Desire> desires = Stubs.stubDesiresRecipe(day, new OutdoorOnlyRecipe());

            when(fetcher.postPlaceInfoBatch(any(), any())).thenReturn(SebStubs.stubPlaceInfoClay01at1830free());

            assertDoesNotThrow(() -> thingy.doWork(desires));

            assertVacancyFound(new VacancyFound(44L, LocalDate.parse(day), T1830, T1930));
        }

        @Tag(RECIPE_BASED)
        @Test
        void secondOption_clayRestAt1900() throws Exception {
            mockOrders(new ArrayList<>());

            String day = DAY_11;
            List<Desire> desires = Stubs.stubDesiresRecipe(day, new OutdoorOnlyRecipe());

            when(fetcher.postPlaceInfoBatch(any(), any())).thenReturn(SebStubs.stubPlaceInfoClay10at1900free());

            assertDoesNotThrow(() -> thingy.doWork(desires));

            Long id53 = Court.C10.getCourtId();
            assertVacancyFound(new VacancyFound(id53, LocalDate.parse(day), T1900, T2000));
        }
    }


    @Test
    void requestedAny_noPriorOrders_allYouAskIsVacant_searches_1_timeAndFinds() {
        mockOrders(new ArrayList<>());
        List<Desire> desires = Stubs.stubDesires(DAY, ANY, Court.getClayIds());

        // whatever you ask, there's a booking
        when(fetcher.postTimeInfoBatch(any(), any(), any())).thenAnswer(e -> stubTimeInfo(e.getArgument(1), e.getArgument(2)));

        assertDoesNotThrow(() -> thingy.doWork(desires));

        fetchesOnce();
        assertFetchedCourts(Court.getClayIds());
        assertFetchedDate(DAY);
        assertFetchedTime("18:00");
        finds();
    }


    static Stream<Order> weight4orders() {
        return Stream.of(
                // CLAY_SUMMER 18:30
                new Order(LocalDate.parse(DAY_11), Court.C01, T1830, T1930),
                new Order(LocalDate.parse(DAY_11), Court.C02, T1830, T1930),
                new Order(LocalDate.parse(DAY_11), Court.C05, T1830, T1930),
                new Order(LocalDate.parse(DAY_11), Court.C06, T1830, T1930),

                // CLAY REST 19:00
                new Order(LocalDate.parse(DAY_11), Court.C03, T1900, T2000),
                new Order(LocalDate.parse(DAY_11), Court.C04, T1900, T2000),
                new Order(LocalDate.parse(DAY_11), Court.C07, T1900, T2000),
                new Order(LocalDate.parse(DAY_11), Court.C08, T1900, T2000),
                new Order(LocalDate.parse(DAY_11), Court.C09, T1900, T2000),
                new Order(LocalDate.parse(DAY_11), Court.C10, T1900, T2000)
        );
    }

    // todo? this test used to take 1 Order from weight 4 category as an argument.
    //    Now it's parameterized to take all 10 (all clay courts) Orders from weight 4 category.
    //    It makes the test longer. Is this diligence necessary/beneficial/reasonable?
    //    edit: it has more sense now that `weight4orders()` is reused. But is it still reasonable?
    //    edit: after some thinking, I think tests should not overlap in things they test.
    //        - if every test checks for all common things, then
    //          - 1. asserts accumulate
    //          - 2. they become more and more like integration tests
    //        - if there's some aspect of code that is important across many tests, make that assertion in 1 test only
    //          - when that code part is broken, only 1 test fails
    //              - that test will be shorter, easier to read and maintain
    //              - if 1 test failed vs many tests, you SHOULD NOT THINK that 1 failed test is less important than 99 tests failed.
    //              - all tests must pass for the system to be considered working and healthy.
    //              - but many small tests are easier to read and write and maintain.
    //     ... so I should delete `weight4orders()` everywhere (or leave it in 1 place only where enumeration of all order inputs is needed).
    //     ... keeping for now, because it's such a nice observation by me.
    @Tag(RECIPE_BASED)
    @ParameterizedTest
    @MethodSource("weight4orders")
    void hasOrderOfWeight4_existsVacancyOfWeight5_doesNotFind(Order order) throws Exception {
        mockOrders(List.of(order));
        List<Desire> desires = Stubs.stubDesiresRecipe(DAY_11, new OutdoorOnlyRecipe());

        // weight 5: Clay 1 at 18:00
        when(fetcher.postPlaceInfoBatch(any(), any())).thenReturn(SebStubs.stubPlaceInfoClay01at1800free());

        assertDoesNotThrow(() -> thingy.doWork(desires));

        assertVacancyNotFound();
    }

    @Tag(RECIPE_BASED)
    @ParameterizedTest
    @MethodSource("weight4orders")
    void hasOrderOfWeight4_existsAnotherVacancyOfWeight4_doesNotFind(Order order) throws Exception {
        mockOrders(List.of(order));
        List<Desire> desires = Stubs.stubDesiresRecipe(DAY_11, new OutdoorOnlyRecipe());

        // weight 4: Clay 10 at 19:00
        when(fetcher.postPlaceInfoBatch(any(), any())).thenReturn(SebStubs.stubPlaceInfoClay10at1900free());

        assertDoesNotThrow(() -> thingy.doWork(desires));

        assertVacancyNotFound();
    }

    @Tag(RECIPE_BASED)
    @ParameterizedTest
    @MethodSource("weight4orders")
    void hasOrderOfWeight4_existsVacancyOfWeight3_finds(Order order) throws Exception {
        mockOrders(List.of(order));
        String day = DAY_11;
        List<Desire> desires = Stubs.stubDesiresRecipe(day, new OutdoorOnlyRecipe());

        // weight 3: Clay 1 at 19:30
        when(fetcher.postPlaceInfoBatch(any(), any())).thenReturn(SebStubs.stubPlaceInfoClay01at1930free());

        assertDoesNotThrow(() -> thingy.doWork(desires));

        Long id44 = Court.C01.getCourtId();
        assertVacancyFound(new VacancyFound(id44, LocalDate.parse(day), T1930, T2030));
    }

    @Tag(RECIPE_BASED)
    @ParameterizedTest
    @MethodSource("weight4orders")
    void hasOrderOfWeight4_existsVacancyOfWeight2_finds(Order order) throws Exception {
        mockOrders(List.of(order));
        String day = DAY_11;
        List<Desire> desires = Stubs.stubDesiresRecipe(day, new OutdoorOnlyRecipe());

        // weight 2: Clay 1 at 19:00
        when(fetcher.postPlaceInfoBatch(any(), any())).thenReturn(SebStubs.stubPlaceInfoClay01at1900free());

        assertDoesNotThrow(() -> thingy.doWork(desires));

        Long id44 = Court.C01.getCourtId();
        assertVacancyFound(new VacancyFound(id44, LocalDate.parse(day), T1900, T2000));
    }


    @Tag(RECIPE_BASED)
    @ParameterizedTest
    @MethodSource("weight2And3Vacancy")
    void hasOrderOfWeight4_existsVacancyOfWeight2and3_sameCourtType_finds2InAnyOrder(PlaceInfoBatchRspDto placeInfoBatchRspDto) {
        String day = DAY_11;
        mockOrders(List.of(new Order(LocalDate.parse(day), Court.C01, T1830, T1930)));
        List<Desire> desires = Stubs.stubDesiresRecipe(day, new OutdoorOnlyRecipe());

        when(fetcher.postPlaceInfoBatch(any(), any())).thenReturn(placeInfoBatchRspDto);

        assertDoesNotThrow(() -> thingy.doWork(desires));

        Long id44 = Court.C01.getCourtId();
        assertVacancyFound(new VacancyFound(id44, LocalDate.parse(day), T1900, T2000));
    }

    static Stream<PlaceInfoBatchRspDto> weight2And3Vacancy() throws Exception {
        return Stream.of(
                SebStubs.stubPlaceInfoClay01at1900free_then_Clay02at1930free(),    // weight 2, then 3
                SebStubs.stubPlaceInfoClay02at1930free_then_Clay01at1900free()     // weight 3, then 2
        );
    }

    @Tag(RECIPE_BASED)
    @ParameterizedTest
    @MethodSource("weight6And7Vacancy")
    void hasOrderOfWeight8_existsVacancyOfWeight6and7_differentCourtTypes_finds6InAnyOrder(PlaceInfoBatchRspDto placeInfoBatchRspDto) {
        String day = DAY_11;
        mockOrders(List.of(new Order(LocalDate.parse(day), Court.G1, T1930, T2030)));
        List<Desire> desires = Stubs.stubDesiresRecipe(day, new OutdoorOnlyRecipe());

        when(fetcher.postPlaceInfoBatch(any(), any())).thenReturn(placeInfoBatchRspDto);

        assertDoesNotThrow(() -> thingy.doWork(desires));

        Long id52 = Court.C09.getCourtId();
        assertVacancyFound(new VacancyFound(id52, LocalDate.parse(day), T1830, T1930));
    }

    static Stream<PlaceInfoBatchRspDto> weight6And7Vacancy() throws Exception {
        return Stream.of(
                SebStubs.stubPlaceInfoClay09at1830free_then_Grass01at1900free(),    // weight 6, then 7
                SebStubs.stubPlaceInfoGrass01at1900free_then_Clay09at1830free()     // weight 7, then 6
        );
    }

    @Tag(RECIPE_BASED)
    @Nested
    class Durations {

        public static final String TODAY = DAY_11;

        ///  The following tests assume getDurationPreference = List.of(90, 60);

        @Nested
        class NoPriorOrder {

            @Test
            void _30Available_doesNotFind() throws Exception {
                mockOrders(new ArrayList<>());
                List<Desire> desires = Stubs.stubDesiresRecipe(TODAY, new OutdoorOnlyRecipe());
                when(fetcher.postPlaceInfoBatch(any(), any())).thenReturn(SebStubs.stubPlaceInfoClay01at1800has30minFree());

                assertDoesNotThrow(() -> thingy.doWork(desires));

                assertVacancyNotFound();
            }

            // todo: a bit reduntant. Or fully redundant. Should I keep it here as well for exhaustiveness reasons? Or remove the other one and keep duratipn-related here only?
            @Test
            void _60Available_finds() throws Exception {
                mockOrders(new ArrayList<>());
                List<Desire> desires = Stubs.stubDesiresRecipe(TODAY, new OutdoorOnlyRecipe());
                when(fetcher.postPlaceInfoBatch(any(), any())).thenReturn(SebStubs.stubPlaceInfoClay01at1800free());

                assertDoesNotThrow(() -> thingy.doWork(desires));

                assertVacancyFound(new VacancyFound(Court.C01.getCourtId(), LocalDate.parse(TODAY), T1800, T1900));
            }

            @Test
            void _90Available_finds() throws Exception {
                mockOrders(new ArrayList<>());
                List<Desire> desires = Stubs.stubDesiresRecipe(TODAY, new OutdoorOnlyRecipe());
                when(fetcher.postPlaceInfoBatch(any(), any())).thenReturn(SebStubs.stubPlaceInfoClay01at1930has90minFree());

                assertDoesNotThrow(() -> thingy.doWork(desires));

                assertVacancyFound(new VacancyFound(Court.C01.getCourtId(), LocalDate.parse(TODAY), T1930, T2100));
            }


            @ParameterizedTest
            @MethodSource("_90and60available_sameWeight_picks90inAnyOrder_args")
            void _90and60available_sameWeight_picks90inAnyOrder(PlaceInfoBatchRspDto dtoStub, VacancyFound vacancyFoundExpected) {
                mockOrders(new ArrayList<>());
                List<Desire> desires = Stubs.stubDesiresRecipe(TODAY, new OutdoorOnlyRecipe());
                when(fetcher.postPlaceInfoBatch(any(), any())).thenReturn(dtoStub);

                assertDoesNotThrow(() -> thingy.doWork(desires));

                assertVacancyFound(vacancyFoundExpected);
            }

            static List<Arguments> _90and60available_sameWeight_picks90inAnyOrder_args() throws Exception {
                return List.of(
                        Arguments.of(SebStubs.stubWeight5_60min90min60min(), new VacancyFound(Court.C01.getCourtId(), LocalDate.parse(TODAY), T2000, T2130)),
                        Arguments.of(SebStubs.stubWeight5_60min60min90min(), new VacancyFound(Court.C09.getCourtId(), LocalDate.parse(TODAY), T1930, T2100))
                );
            }

            @Test
            void _120Available_finds90() throws Exception {
                mockOrders(new ArrayList<>());
                List<Desire> desires = Stubs.stubDesiresRecipe(TODAY, new OutdoorOnlyRecipe());
                when(fetcher.postPlaceInfoBatch(any(), any())).thenReturn(SebStubs.stubPlaceInfoClay09at1930has120minFree());

                assertDoesNotThrow(() -> thingy.doWork(desires));

                assertVacancyFound(new VacancyFound(Court.C09.getCourtId(), LocalDate.parse(TODAY), T1930, T2100));
            }

        }

        @Nested
        class PriorOrderOf60Min {

            private List<Desire> desires;

            @BeforeEach
            void setUp() {
                mockOrders(List.of(new Order(LocalDate.parse(TODAY), Court.G1, T1800, T1900)));       // last weight
                desires = Stubs.stubDesiresRecipe(TODAY, new OutdoorOnlyRecipe());
            }

            @Test
            void _60Available_finds() throws Exception {
                when(fetcher.postPlaceInfoBatch(any(), any())).thenReturn(SebStubs.stubPlaceInfoClay01at1800free());
                assertDoesNotThrow(() -> thingy.doWork(desires));
                assertVacancyFound(new VacancyFound(Court.C01.getCourtId(), LocalDate.parse(TODAY), T1800, T1900));
            }

            @Test
            void _90Available_finds() throws Exception {
                when(fetcher.postPlaceInfoBatch(any(), any())).thenReturn(SebStubs.stubPlaceInfoClay01at1930has90minFree());
                assertDoesNotThrow(() -> thingy.doWork(desires));
                assertVacancyFound(new VacancyFound(Court.C01.getCourtId(), LocalDate.parse(TODAY), T1930, T2100));
            }

        }

        @Nested
        class PriorOrderOf90Min {

            private List<Desire> desires;

            @BeforeEach
            void setUp() {
                mockOrders(List.of(new Order(LocalDate.parse(TODAY), Court.G1, T1800, T1930)));       // last weight
                desires = Stubs.stubDesiresRecipe(TODAY, new OutdoorOnlyRecipe());
            }

            @Test
            void _60Available_doesNotFind() throws Exception {
                when(fetcher.postPlaceInfoBatch(any(), any())).thenReturn(SebStubs.stubPlaceInfoClay01at1800free());
                assertDoesNotThrow(() -> thingy.doWork(desires));
                assertVacancyNotFound();
            }

            @Test
            void _90Available_finds() throws Exception {
                when(fetcher.postPlaceInfoBatch(any(), any())).thenReturn(SebStubs.stubPlaceInfoClay01at1930has90minFree());
                assertDoesNotThrow(() -> thingy.doWork(desires));
                assertVacancyFound(new VacancyFound(Court.C01.getCourtId(), LocalDate.parse(TODAY), T1930, T2100));
            }

        }
    }


    // Maybe we'll need this when dealing with orders (cancelling, reminding of cancellation). Because they are exactly 48-hour long.
    // todo move to OrderTest? It will only be used for orders
    @MockitoSettings(strictness = Strictness.LENIENT)
    @Nested
    class _48Hours {

        @Test
        void getNow_getsNow() {
            assertEquals(LocalDateTime.parse("2023-10-01T12:00:00"), thingy.getNow());
        }

        @Test
        void getExactly48HoursAgo() {
            assertEquals(LocalDateTime.parse("2023-09-29T12:00:00"), thingy.get48HoursAgo());
        }

        @Test
        void get1secondLessThan48HoursAgo() {
            assertEquals(LocalDateTime.parse("2023-09-29T12:00:01"), thingy.get48HoursAgo().plusSeconds(1));
        }

        @Test
        void get1secondMoreThan48HoursAgo() {
            assertEquals(LocalDateTime.parse("2023-09-29T11:59:59"), thingy.get48HoursAgo().minusSeconds(1));
        }
    }







    // todo   move to nested "extension interest" section?
    // todo   maybe also do tests that have earlier, but test asks for LATER -- does not find
    @ParameterizedTest
    @CsvSource({"18:00", "19:30"})
    void requestedAny_canFindBothEarlierOrLater(String newTime) {    // todo make LocalTime
        mockOrders(stubOrders(Court.H02, DAY, "18:30", "19:30"));       // todo: orders? or reservations?
        List<Desire> desires = Stubs.stubDesires(DAY, ANY, Court.getHardIds());

        Court h02 = Court.H02;
        stubOccupiedExcept(List.of(h02.getCourtId()), h02, LocalTime.parse(newTime), 30L);

        assertDoesNotThrow(() -> thingy.doWork(desires));

//        fetchesOnceOrTwice(); // could be many times because searches for earlier, then later. But could be the other way around and search count can change depending on where the vacancy is
        fetchesAtLeastOnce();
        assertFetchedCourts(List.of(Court.H02.getCourtId()));
        assertFetchedDate(DAY);
        assertFetchedTime(newTime);
        finds();
    }


    // todo leave this test in here or move to DesireOrderPairer? handling logic is in there
    @Disabled("currently the only test unhappy with a @BeforeEach mock of fetcher.postPlaceInfoBatch(any(), any()). Move to DesireOrderPairerTest instead of fixing")
    @ParameterizedTest
    @EnumSource(value = ExtensionInterest.class, names = {"EARLIER", "LATER"})
    void requestedEarlierOrLater_dontHaveOrders_throws(ExtensionInterest interest) {
        List<Desire> desires = Stubs.stubDesires(DAY, interest, Court.getHardIds());
        assertThrows(EarlierOrLaterDesireMustHaveOwnOrderException.class, () -> thingy.doWork(desires));
    }


    @ParameterizedTest
    @CsvSource({"30", "60", "90", "120"})
    void requestedAny_noPriorOrders_findsAnyDurationSlotExcept30min(int minutes) {
        mockOrders(new ArrayList<>());
        List<Desire> desires = Stubs.stubDesires(DAY, ANY, Court.getClayIds());

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
        List<Desire> desires = Stubs.stubDesires(DAY, earlierOrAny, Court.getHardIds());

        String vacancyAt1700 = "17:00";
        stubOccupiedExcept(List.of(h02.getCourtId()), h02, LocalTime.parse(vacancyAt1700), 30L);

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
        List<Desire> desires = Stubs.stubDesires(DAY, laterOrAny, Court.getHardIds());

        String vacancyAt1900 = "19:00";
        stubOccupiedExcept(List.of(h02.getCourtId()), h02, LocalTime.parse(vacancyAt1900), 30L);

        assertDoesNotThrow(() -> thingy.doWork(desires));

        fetchesAtLeastOnce();
        assertFetchedCourts(List.of(h02.getCourtId()));
        assertFetchedDate(DAY);
        assertFetchedTime(vacancyAt1900);
        finds();
    }

    // todo: do we need this? it tests if 2 tests work together or if there's conflict.
    //  If there's conflict, DesireOrderPairer should find it.
    //  Should this test class care? It's different layer problems, no?
    //  This class works on a 1 desire (with or without ALREADY CORRECTLY paired order) --> doWork --> finds reservation
    @Test
    void requestedAnyOutsideAndNoneInside_have1insideOrder_insideDoesNotSearch_outsideDoesSearch() {
        mockOrders(stubOrders(Court.H02, DAY, "17:30", "19:00"));
        List<Desire> desires = stubDesires(
                new Desire(LocalDate.parse(DAY), NONE, Court.getIndoorIds()),
                new Desire(LocalDate.parse(DAY), ANY, Court.getOutdoorIds())
        );

        when(fetcher.postTimeInfoBatch(any(), any(), any())).thenReturn(stubTimeInfoOccupied());

        assertDoesNotThrow(() -> thingy.doWork(desires));
        fetches4times();    // does not search for indoors because NONE; searches for brand-new outdoor because ANY
        assertFetchedCourts(Court.getOutdoorIds());
        doesNotFind();
    }

    // todo marker comment. This test has overlap with DesireOrderPairerTest. But it also checks that H01 and G1 are both searched for.
    //   update with fresh eyes: it should not matter that there's 2 similar desires on same day.
    //   DesireOrderPairer should pair correctly. This test class should test 1 desire at a time.
    @ParameterizedTest
    @EnumSource(value = ExtensionInterest.class, names = {"EARLIER", "LATER"})
    void requestedImprovementForBothInsideAndOutside_findsBoth(ExtensionInterest interest) {
        mockOrders(stubOrders(
                new Order(LocalDate.parse(DAY), Court.H01, LocalTime.parse("18:00"), T1900),
                new Order(LocalDate.parse(DAY), Court.G1, LocalTime.parse("18:00"), T1900)
        ));
        List<Desire> desires = stubDesires(
                new Desire(LocalDate.parse(DAY), interest, Court.getIndoorIds()),
                new Desire(LocalDate.parse(DAY), interest, Court.getOutdoorIds())
        );

        // whatever you ask, there's a booking
        when(fetcher.postTimeInfoBatch(any(), any(), any())).thenAnswer(e -> stubTimeInfo(e.getArgument(1), e.getArgument(2)));

        assertDoesNotThrow(() -> thingy.doWork(desires));
        fetchesAtLeastOnce();
        finds2Vacancies();

        List<List<Long>> courtsCaptured = courtsCaptor.getAllValues();
        assertEquals(List.of(Court.H01.getCourtId()), courtsCaptured.get(0));       // todo make a test with effectively-adjacent scenario? Would it be redundant?    assertEquals(Court.getHardIds(), courtsCaptured.get(0))  Probably redundant. Because we just need 1 test to check that both H01 and G1 are searched for. Other scenarios are the same regardless if there's 1 or 2 desires/orders per day.
        assertEquals(List.of(Court.G1.getCourtId()), courtsCaptured.get(1));
    }

    /// ///// START OF  === PRESERVE-BOOKING-LENGTH CHANGE ===  ///////////////////////////////////////////////////
    /// /////////////////////////////////////////////////////////////////////////////////////////////////////////


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
        List<Desire> desires = Stubs.stubDesires(DAY, interest, Court.getHardIds());

        LocalTime timeFromVacancy = LocalTime.parse(searchFrom);
        stubOccupiedExcept(Court.getHardIds(), Court.H01, timeFromVacancy, vacancyDuration);

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

        BookingConfigurator configurator = new BookingConfigurator(audioPlayer, fetcher, CLOCK);
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
        BookingConfigurator configurator = new BookingConfigurator(audioPlayer, fetcher, CLOCK);
        configurator.setEarlyBird(LocalTime.parse("17:00"));
        thingy = new DesiresIteratorThingy(configurator);

        String timeFrom = "17:00";  // = orderFrom
        String searchFrom = "19:30";    // 19:30 will always have a gap. e.g. even if 120 min, end of vacancy will be 19:00 which is 30 min before 19:30
        searchNonAdjacent(timeFrom, interest, orderDuration, vacancyDuration, shouldFind, searchFrom);
    }

    private void searchNonAdjacent(String orderFrom, ExtensionInterest interest, long orderDuration, long vacancyDuration, boolean shouldFind, String searchFrom) {
        mockOrders(stubOrders(Court.H02, orderFrom, orderDuration));

        List<Desire> desires = Stubs.stubDesires(DAY, interest, Court.getNonSquashIds());

        List<Long> allCourts = new ArrayList<>(Court.getNonSquashIds());
        LocalTime timeFromVacancy = LocalTime.parse(searchFrom);
        stubOccupiedExcept(allCourts, Court.H02, timeFromVacancy, vacancyDuration);

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


    /// ///// END OF  === DOUBLE-BOOKING (indoors + outdoors) ===  ///////////////////////////////////////////////////
    /// /////////////////////////////////////////////////////////////////////////////////////////////////////////


    private void stubOccupiedExcept(List<Long> requestedCourts, Court returnedCourt, LocalTime time, long vacancyDuration) {
        LocalDate day = LocalDate.parse(DAY);
        // in mocking, last mock matters. So, all are made to be empty, but then, if second mock is more specific, only second one will be in effect.
        when(fetcher.postTimeInfoBatch(any(), any(), any())).thenReturn(stubTimeInfoOccupied());

        // todo: add validation? requestedCourts and returnedCourt.getCourtId() should be from the same pool, e.g. indoorCourts, H01 or outdoorCourts, G01
        when(fetcher.postTimeInfoBatch(requestedCourts, day, time))
                .thenReturn(stubTimeInfo(returnedCourt.getCourtId(), day, time, vacancyDuration));
    }

    private void finds() {
        verify(audioPlayer).chimeIfNecessary();
    }

    private void finds2Vacancies() {
        verify(audioPlayer, times(2)).chimeIfNecessary();
    }

    private void doesNotFind() {
        verify(audioPlayer, never()).chimeIfNecessary();
    }

    private void mockOrders(List<Order> orders) {
        orderConverterMockedStatic.when(() -> SebOrderConverter.toOrders(any())).thenReturn(orders);
    }

    private static List<Desire> stubDesires(Desire... desires) {
        return new ArrayList<>(List.of(desires));
    }

    private void fetchesAtLeastOnce() {
        verify(fetcher, atLeastOnce()).postTimeInfoBatch(courtsCaptor.capture(), dateCaptor.capture(), timeCaptor.capture());
    }

    private void fetchesOnce() {
        verify(fetcher).postTimeInfoBatch(courtsCaptor.capture(), dateCaptor.capture(), timeCaptor.capture());
    }

    private void fetchesOncePlaces() {
        verify(fetcher).postPlaceInfoBatch(datesCaptor.capture(), placesCaptor.capture());
    }

    private void fetchesPlaces(int invocationCount) {
        verify(fetcher, times(invocationCount)).postPlaceInfoBatch(datesCaptor.capture(), placesCaptor.capture());
    }

    private void fetchesOnceOrTwice() {
        verify(fetcher, atLeast(1)).postTimeInfoBatch(courtsCaptor.capture(), dateCaptor.capture(), timeCaptor.capture());
        verify(fetcher, atMost(2)).postTimeInfoBatch(courtsCaptor.capture(), dateCaptor.capture(), timeCaptor.capture());
    }

    private void fetchesTwice() {
        verify(fetcher, times(2)).postTimeInfoBatch(courtsCaptor.capture(), dateCaptor.capture(), timeCaptor.capture());
    }

    private void fetchesThrice() {
        verify(fetcher, times(3)).postTimeInfoBatch(courtsCaptor.capture(), dateCaptor.capture(), timeCaptor.capture());
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

    private void assertVacancyFound(VacancyFound expectedVacancyFound) {
        verify(thingy).emptyMethodToCaptureArgument(vacancyFoundCaptor.capture());
        VacancyFound actualVacancyFound = vacancyFoundCaptor.getValue();
        assertEquals(expectedVacancyFound, actualVacancyFound);
    }

    private void assertVacancyNotFound() {
        verify(thingy).emptyMethodToCaptureArgument(vacancyFoundCaptor.capture());
        assertNull(vacancyFoundCaptor.getValue());
    }

    private void assertCourtDtosFilteredFor1day() {
        verify(thingy, atLeastOnce()).findVacancy(any(), courtDtosCaptor.capture());
        List<DataInner> dtos = courtDtosCaptor.getValue();
        long count = dtos.stream().map(DataInner::getDate).distinct().count();
        System.out.println("---- count: " + count);
        boolean only1day = count == 1;

        assertTrue(only1day);
    }
}