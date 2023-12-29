package com.butkus.tenniscrawler;

import com.butkus.tenniscrawler.rest.SebFetcher;
import com.butkus.tenniscrawler.rest.SebOrderConverter;
import com.butkus.tenniscrawler.rest.orders.Order;
import com.butkus.tenniscrawler.rest.orders.OrdersRspDto;
import com.butkus.tenniscrawler.rest.placeinfobatch.PlaceInfoBatchRspDto;
import com.butkus.tenniscrawler.rest.timeinfobatch.TimeInfoBatchRspDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class SebRestTest {

    @Autowired
    SebFetcher fetcher;

    @Test
    void testOptionsTimeInfoBatch() {
        HttpStatus status = fetcher.optionsPlaceInfoBatch();
        assertEquals(HttpStatus.OK, status);
    }

    @Test
    void testTimeInfoBatch() {
        List<Long> ids = Court.getIds();
        TimeInfoBatchRspDto timeInfoBatchRspDto = fetcher.postTimeInfoBatch(ids, LocalDate.parse("2023-09-14"), LocalTime.parse("19:00"));
        assertNotNull(timeInfoBatchRspDto);
    }

    @Test
    void postPlaceInfoBatch() {
        List<String> dates = List.of("2023-09-12", "2023-09-13", "2023-09-14", "2023-09-11", "2023-09-18");
//        List<LocalDate> range = SebPlaceInfoConverter.getDateRange("2023-09-18", "2023-09-24");
        PlaceInfoBatchRspDto actual = fetcher.postPlaceInfoBatch(dates, CourtType.getIds());
        assertNotNull(actual);
        String from = "20:00";
        String to = "20:30";
        System.out.printf("hasVacancies(%s - %s) = %s%n", from, to, actual.hasVacancies(from, to));
    }

    // todo fixme (or remove)
    @Test
    void allInOne() {
//        List<String> dates = List.of("2023-09-12", "2023-09-13", "2023-09-14", "2023-09-11", "2023-09-18");
//        List<LocalDate> dates = SebPlaceInfoConverter.getDateRange("2023-09-30", "2023-09-30");
//        PlaceInfoBatchRspDto placeInfoBatchRspDto = fetcher.postPlaceInfoBatch(dates, CourtType.getIds());

//        boolean hasVacancies = placeInfoBatchRspDto.hasVacancies("17:00", "20:00");

//        if (!hasVacancies) fail("=== On 2023-09-23 had vacancies, now NO vacancies ===");
//        assertTrue(timeFound(Court.getIds(), dates, "18:00"));
    }

//    private boolean timeFound(List<Integer> courts, List<String> range, String time) {
//        for (String date : range) {
//            TimeInfoBatchRspDto timeResp = fetcher.postTimeInfoBatch(courts, date, time);
//            if (!"success".equals(timeResp.getStatus())) continue;      // todo should indicate api error
//            for (DataTimeInfo datum : timeResp.getData()) {
//                if (!datum.getAviableDurations().isEmpty()) return true;
//            }
//        }
//        return false;
//    }

    @Test
    void getOrders() {
        OrdersRspDto dtoOrders = fetcher.getOrders("2023-09-16", "2023-09-30");

        List<Order> convertedOrders = SebOrderConverter.toOrders(dtoOrders);

        System.out.println("dtoOrders = " + dtoOrders);
        System.out.println("convertedOrders = " + convertedOrders);
        assertNotNull(dtoOrders);
    }

}