package com.butkus.tenniscrawler;

import com.butkus.tenniscrawler.rest.SebFetcher;
import com.butkus.tenniscrawler.rest.orders.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static java.time.ZoneOffset.UTC;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LegacySearchTest {

    public static final LocalDate TODAY = LocalDate.parse("2023-10-01");
    public static final LocalTime T1800 = LocalTime.parse("18:00");
    public static final LocalTime T1830 = LocalTime.parse("18:30");
    public static final LocalTime T1900 = LocalTime.parse("19:00");
    public static final LocalTime T1930 = LocalTime.parse("19:30");
    public static final LocalTime T2000 = LocalTime.parse("20:00");
    
    @Mock private AudioPlayer audioPlayer;
    @Mock private SebFetcher fetcher;

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2023-10-01T12:00:00Z"), UTC);
    
    private BookingConfigurator bookingConfigurator;
    private Stubs stubs;

    @BeforeEach
    void setUp() {
        bookingConfigurator = new BookingConfigurator(audioPlayer, fetcher, CLOCK);
        stubs = new Stubs(fetcher, TODAY);
    }

    @Test
    void searchForEarlier_finds_returnsVacancyFound() {
        Desire desire = stubDesireForOrder(Court.C01, T1900, T2000, ExtensionInterest.EARLIER);
        LegacySearch search = new LegacySearch(bookingConfigurator, desire);

        stubs.stubOccupiedExcept(List.of(Court.C01.getCourtId()), Court.C01, T1830, 30L);

        VacancyFound expected = new VacancyFound(Court.C01.getCourtId(), TODAY, T1830, T1900);
        VacancyFound actual = search.searchForEarlier();
        assertEquals(expected, actual);
    }

    @Test
    void searchForLater_finds_returnsVacancyFound() {
        Desire desire = stubDesireForOrder(Court.C01, T1800, T1900, ExtensionInterest.LATER);
        LegacySearch search = new LegacySearch(bookingConfigurator, desire);

        stubs.stubOccupiedExcept(List.of(Court.C01.getCourtId()), Court.C01, T1900, 30L);

        VacancyFound expected = new VacancyFound(Court.C01.getCourtId(), TODAY, T1900, T1930);
        VacancyFound actual = search.searchForLater();
        assertEquals(expected, actual);
    }

    @Test
    void searchForEarlier_doesNotFind_returnsNull() {
        Desire desire = stubDesireForOrder(Court.C01, T1900, T2000, ExtensionInterest.EARLIER);
        LegacySearch search = new LegacySearch(bookingConfigurator, desire);

        // whatever you ask, there's NO booking
        when(fetcher.postTimeInfoBatch(any(), any(), any())).thenReturn(Stubs.stubTimeInfoOccupied());

        assertNull(search.searchForEarlier());
    }

    @Test
    void searchForLater_doesNotFind_returnsNull() {
        Desire desire = stubDesireForOrder(Court.C01, T1800, T1900, ExtensionInterest.LATER);
        LegacySearch search = new LegacySearch(bookingConfigurator, desire);

        // whatever you ask, there's NO booking
        when(fetcher.postTimeInfoBatch(any(), any(), any())).thenReturn(Stubs.stubTimeInfoOccupied());

        assertNull(search.searchForLater());
    }


    private static Desire stubDesireForOrder(Court court, LocalTime from, LocalTime to, ExtensionInterest interest) {
        Desire desire = new Desire(TODAY, interest, Court.getCarpetIds());
        Order order = new Order(TODAY, court, from, to);
        desire.setOrder(order);
        return desire;
    }

}