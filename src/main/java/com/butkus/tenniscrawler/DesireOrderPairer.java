package com.butkus.tenniscrawler;

import com.butkus.tenniscrawler.rest.orders.Order;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.butkus.tenniscrawler.ExtensionInterest.EARLIER;
import static com.butkus.tenniscrawler.ExtensionInterest.LATER;

@AllArgsConstructor
public class DesireOrderPairer {

    private static final List<Long> INDOOR_IDS = Court.getIndoorIds();    // cascading style 1 (more specific)
    private static final List<Long> OUTDOOR_IDS = Court.getOutdoorIds();  // cascading style 1 (more specific)
    private static final List<Long> ALL_IDS = Court.getNonSquashIds();    // cascading style 2 (less specific)  // todo what about squash booking same time as tennis?
    // the only arbitrary-courtId-set allowing method is
    // DesireMaker.addNext(int count, DayOfWeek dayOfWeek, Desire draft)
    // it allows to bring-your-own Desire, meaning you can bring-your-own List<Long> courtIds
    // it currently is **private** which means we are gatekeeping the List<Long> courtIds values to indoor- and outdoor-only
    // if we make it public -- or add another similar that allows supplying your own Desire or courtIds list
    // then we'll need to make a whole suite of tests to check (not limited to):
    //  - overlap (e.g. 2 courts in the shade + all outdoors)
    //  - correct priority order e.g. (assume same day):
    //     - 2 shade + 2 grass --> how to assign? first in clay, second in grass?
    //     - 2 shade + all outside, except those 2 in shade --> combined full outdoor coverage, but no overlap. Need new way of categorizing

    private List<Desire> desires;
    private List<Order> orders;

    // first search for most specific desires -- inside or outside
    // then, for less specific desires -- all courts

    public void pair() {
        validateOrders();
        pairOrders();
        validateDesires();
    }

    private void pairOrders() {
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

    private void validateOrders() {
        Map<LocalDate, List<Order>> collect = orders.stream()
                .collect(Collectors.groupingBy(Order::getDate));
        for (Map.Entry<LocalDate, List<Order>> entry : collect.entrySet()) {
            List<Order> ordersByDay = entry.getValue();

            List<Order> indoor = new ArrayList<>();
            List<Order> outdoor = new ArrayList<>();
            List<Order> all = new ArrayList<>();

            for (Order order : ordersByDay) {
                Long courtId = order.getCourt().getCourtId();
                if (INDOOR_IDS.contains(courtId)) indoor.add(order);
                if (OUTDOOR_IDS.contains(courtId)) outdoor.add(order);
                if (ALL_IDS.contains(courtId)) all.add(order);

                if (indoor.size() > 1) throw new DuplicateOrdersException("More than 1 indoor Order for " + order.getDate());
                if (outdoor.size() > 1) throw new DuplicateOrdersException("More than 1 outdoor Order for " + order.getDate());

                // todo sumcheck -- if does not sumcheck --> new category introduced but not adhered to order
                //  or maybe current tests cover that in different terms (in away other than indoor+outdoor=all
                //  or maybe current tests need some addition and it will be covered
            }
        }
    }

    private void validateDesires() {
        for (Desire desire : desires) {
            boolean isEarlierOrLater = desire.getExtensionInterest() == EARLIER || desire.getExtensionInterest() == LATER;
            boolean noOrder = desire.getOrder() == null;
            if (isEarlierOrLater && noOrder) {
                throw new EarlierOrLaterDesireMustHaveOwnOrderException("Desire: " + desire);
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
