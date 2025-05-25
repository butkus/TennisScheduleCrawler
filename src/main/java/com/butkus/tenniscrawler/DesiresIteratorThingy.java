package com.butkus.tenniscrawler;

import com.butkus.tenniscrawler.rest.SebOrderConverter;
import com.butkus.tenniscrawler.rest.orders.Order;
import com.butkus.tenniscrawler.rest.orders.OrdersRspDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
@Component
public class DesiresIteratorThingy {

    private final BookingConfigurator configurator;


    public void doWork(List<Desire> desires) {

        /////////////////////////////// OPTIMIZATION //////
//        List<String> dates = SebPlaceInfoConverter.getDateRange("2023-09-23", "2023-09-24");
//        PlaceInfoBatchRspDto placeInfoBatchRspDto = fetcher.postPlaceInfoBatch(dates, CourtType.getIds());
//        boolean hasVacancies = placeInfoBatchRspDto.hasVacancies("18:00", "20:00");
        /////////////////////////////// OPTIMIZATION //////

        // todo branstorms here
//        bookables.iterateThroughtAll();   // or just iterate()
//        calender.printReadableCalendar();  or  orders.printCalendar();

//        soundPlayer/bell/vacancyIndicator? should register what day/order it beeped on. And later pass that to calender so that it can be printed in a standout fashion

        configurator.resetAudioPlayer();

        LocalDate min = desires.stream().map(Desire::getDate).min(Comparator.naturalOrder()).orElseThrow(() -> new RuntimeException("no min date found"));
        LocalDate max = desires.stream().map(Desire::getDate).max(Comparator.naturalOrder()).orElseThrow(() -> new RuntimeException("no max date found"));
        OrdersRspDto ordersDto = configurator.getFetcher().getOrders(min.toString(), max.toString());
        List<Order> orders = SebOrderConverter.toOrders(ordersDto);

        // todo if "bandyti parduoti", order will have "sab_parduodamas":1

        DesireOrderPairer pairer = new DesireOrderPairer(desires, orders);
        pairer.pair();

//        System.out.println("--- desires:");
//        desires.forEach(e -> System.out.println("  -- Desire: " + e));
//        System.out.println();

        for (Desire desire : desires) {
            Vacancy vacancy = new Vacancy(desire, configurator);
            vacancy.find();
        }

        Calendar.printCalendar(orders, desires);
    }





}
