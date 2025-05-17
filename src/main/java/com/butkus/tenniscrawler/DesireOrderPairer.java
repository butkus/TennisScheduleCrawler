package com.butkus.tenniscrawler;

import com.butkus.tenniscrawler.rest.orders.Order;
import lombok.AllArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

// todo Do I use DesireOrderPairer for functionality or just validation?
@AllArgsConstructor
public class DesireOrderPairer {

    private static final List<Long> INDOOR_IDS = Court.getIndoorIds();    // cascading style 1 (more specific)
    private static final List<Long> OUTDOOR_IDS = Court.getOutdoorIds();  // cascading style 1 (more specific)
    private static final List<Long> ALL_IDS = Court.getNonSquashIds();    // cascading style 2 (less specific)

    private List<Desire> desires;
    private List<Order> orders;

    // first search for most specific desires -- inside or outside
    // then, for less specific desires -- all courts

    public void pair() {
        for (Order order : orders) {
            List<Desire> desiresByOut = findMatchingDesire(order, OUTDOOR_IDS);
            List<Desire> desiresByIn = findMatchingDesire(order, INDOOR_IDS);
            List<Desire> desiresByAll = findMatchingDesire(order, ALL_IDS);

            // outside and inside are (currently) most granular option, so must be 1 or 0 such desires per 1 day
            if (desiresByOut.size() > 1) throw new DuplicateDesiresException("More than 1 desires for Order: " + order);
            if (desiresByIn.size() > 1) throw new DuplicateDesiresException("More than 1 desires for Order: " + order);

            Desire desireByOut = desiresByOut.isEmpty() ? null : desiresByOut.get(0);       // may contain 0 or 1
            Desire desireByIn = desiresByIn.isEmpty() ? null : desiresByIn.get(0);          // may contain 0 or 1
            Desire desireByAll = desiresByAll.isEmpty() ? null : desiresByAll.get(0);       // may contain 0, 1, or more

            if (desireByOut != null) {
                match(desireByOut, order);
            } else if (desireByIn != null) {
                match(desireByIn, order);
            } else if (desireByAll != null) {
                match(desireByAll, order);
            } else {
                throw new OrderWithoutDesireException("No desire found in any category. Order: " + order);
            }
        }
    }

    private void match(Desire desire, Order order) {
        desire.setOrder(order);
    }

    private List<Desire> findMatchingDesire(Order order, List<Long> courtListCategory) {
        return desires.stream()
                .filter(desire -> new HashSet<>(courtListCategory).containsAll(desire.getCourts()))
                .filter(desire -> desire.getDate().equals(order.getDate()))
                .filter(desire -> desire.getCourts().contains(order.getCourt().getCourtId()))
                .collect(Collectors.toList());
    }
}
