package com.butkus.tenniscrawler.rest;

import com.butkus.tenniscrawler.Court;
import com.butkus.tenniscrawler.rest.orders.Order;
import com.butkus.tenniscrawler.rest.orders.OrdersRspDto;
import com.butkus.tenniscrawler.rest.orders.Result;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class SebOrderConverter {

    // todo collection
    public static List<Order> toOrders(OrdersRspDto dtos) {
        List<Order> orders = new ArrayList<>();
        for (Result dto : dtos.getData().getResults()) {
            Order order = getOrder(dto);
            orders.add(order);
        }
        List<Order> coalescedOrders = coalesce(orders);

        return coalescedOrders;
    }

    private static Order getOrder(Result dto) {
        Court court = Court.getByName(dto.getPaslPavadinimas());
        Order order = new Order();
        order.setCourt(court);

        String[] dateAndTimeFrom = dto.getSasiGaliojanuo().split(" ");
        LocalDate dateFrom = LocalDate.parse(dateAndTimeFrom[0]);
        LocalTime timeFrom = LocalTime.parse(dateAndTimeFrom[1]);

        String[] dateAndTimeUntil = dto.getIki().split(" ");
        LocalDate dateUntil = LocalDate.parse(dateAndTimeUntil[0]);
        LocalTime timeUntil = LocalTime.parse(dateAndTimeUntil[1]);

        String msg = String.format("Reservation error: dateFrom (%s) != dateUntil (%s)", dateFrom, dateUntil);
        if (!dateFrom.equals(dateUntil)) throw new RuntimeException(msg);

        order.setDate(dateFrom);
        order.setTimeFrom(timeFrom);
        order.setTimeTo(timeUntil);
        return order;
    }

    public static List<Order> coalesce(List<Order> allOrders) {
        if (allOrders == null) return allOrders;

        List<Collection<List<Order>>> daysWithCourtsWithReservations = allOrders.stream()
                .collect(Collectors.groupingBy(Order::getDate, Collectors.groupingBy(Order::getCourt)))
                .values().stream()
                .map(Map::values)
                .collect(Collectors.toList());

        for (Collection<List<Order>> dayWithCourtsWithReservations : daysWithCourtsWithReservations) {
            for (List<Order> dayWithCourtWithReservations : dayWithCourtsWithReservations) {
                coalesceForParticularDayParticularCourt(dayWithCourtWithReservations);
            }
        }

        return daysWithCourtsWithReservations.stream()
                .flatMap(Collection::stream)
                .flatMap(List::stream)
                .sorted(Comparator.comparing(Order::getDate).thenComparing(Order::getCourt))
                .collect(Collectors.toList());
    }

    // todo maybe public static void?
    public static List<Order> coalesceForParticularDayParticularCourt(List<Order> orders) {
        if (orders.size() == 1) return orders;

        orders.sort(Comparator.comparing(Order::getTimeFrom));

        boolean traversed = false;
        int i = 0;
        int n = orders.size();
        do {
            Order first = orders.get(i);
            Order second = orders.get(i + 1);
            LocalTime firstEnds = first.getTimeTo();
            LocalTime secondBegins = second.getTimeFrom();
            if (firstEnds.equals(secondBegins)) {
                first.setTimeTo(second.getTimeTo());
                orders.remove(i + 1);
                n = orders.size();
            } else {
                i++;
            }
            if (n == 1 || i == n - 1) traversed = true;    // n - 1 is taken instead of n because iteration takes 2 elements: i, and i+1
        } while (!traversed);

        return orders;
    }
}
