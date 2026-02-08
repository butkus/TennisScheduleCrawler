package com.butkus.tenniscrawler;

import com.butkus.tenniscrawler.rest.SebFetcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class VacancyTest {

    public static final String DAY_1 = "2023-10-01";
    public static final String DAY_2 = "2023-10-02";
    public static final String DAY_3 = "2023-10-03";
    public static final String DAY_4 = "2023-10-04";

    public static final LocalTime T1800 = LocalTime.parse("18:00");
    public static final LocalTime T1830 = LocalTime.parse("18:30");
    public static final LocalTime T1900 = LocalTime.parse("19:00");
    public static final LocalTime T1930 = LocalTime.parse("19:30");
    public static final LocalTime T2000 = LocalTime.parse("20:00");
    public static final LocalTime T2030 = LocalTime.parse("20:30");
    public static final LocalTime T2100 = LocalTime.parse("21:00");
    public static final LocalTime T2130 = LocalTime.parse("21:30");

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2023-10-01T12:00:00Z"), ZoneId.systemDefault());

    BookingConfigurator configurator;

    @Mock AudioPlayer audioPlayer;
    @Mock SebFetcher fetcher;

    @BeforeEach
    void setUp() {
        configurator = new BookingConfigurator(audioPlayer, fetcher, CLOCK);
    }

    @Nested
    @MockitoSettings(strictness = Strictness.LENIENT)
    class VolatilePeriod {

        // Volatile Period is technically 48 hours.
        // But a day 48 hours from now will contain some sub-48-hour vacancies and some over-48-hour vacancies.
        // Therefore, a whole day should be considered as volatile.

        @Nested
        class DateCheck {

            @ParameterizedTest
            @CsvSource({DAY_1, DAY_2, DAY_3})
            void today_tomorrow_dayAfterTomorrow_isVolatile(String day) {
                Desire desire = new Desire(LocalDate.parse(day), new IndoorWeekend());
                Vacancy vacancy = new Vacancy(desire, configurator);
                assertTrue(vacancy.isVolatile());
            }

            @Test
            void day3AfterToday_notVolatile() {
                Desire desire = new Desire(LocalDate.parse(DAY_4), new IndoorWeekend());
                Vacancy vacancy = new Vacancy(desire, configurator);
                assertFalse(vacancy.isVolatile());
            }
        }

//        @Test
//        void whenTimetableFindsMatch_additionalTimeInfoRequestIsMade() {
//            mockOrders(List.of(new Order(LocalDate.parse(DAY_1), Court.G1, T1800, T1900)));
//            List<Desire> desires = Stubs.stubDesiresRecipe(DAY_1, new OutdoorOnlyRecipe());
//
//            assertDoesNotThrow(() -> thingy.doWork(desires));
//        }

        // todo whenTimetableDoesNotFindMatch_TimeInfoRequestIsNotMade

    }


}