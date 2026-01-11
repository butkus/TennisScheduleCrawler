package com.butkus.tenniscrawler.desireorderpairer;

import com.butkus.tenniscrawler.Desire;
import com.butkus.tenniscrawler.rest.orders.Order;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@NoArgsConstructor
@Data
@Accessors(chain = true, fluent = true)
public class DesireOrderPairTestCase {
    Desire desireIn;
    Desire desireOut;
    Order order;
    Desire expected;
    Desire unexpected;

    // todo make it fail if `build()` is not called

    public DesireOrderPairTestCase build() {
        desireIn = cloneDesire(desireIn);
        desireOut = cloneDesire(desireOut);
        return this;
    }

    private Desire cloneDesire(Desire desire) {
        Desire clone = desire.toBuilder().build();
        desire = clone;
        if (desire.equals(expected)) {
            expected = clone;
        } else if (desire.equals(unexpected)) {
            unexpected = clone;
        }
        return clone;
    }
}
