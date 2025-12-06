package com.butkus.tenniscrawler;

import lombok.Getter;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public abstract class Recipe implements Iterator<Map.Entry<Integer, List<CourtTypeAtHour>>> {

    @Getter
    private final Map<Integer, List<CourtTypeAtHour>> map;
    private final Iterator<Map.Entry<Integer, List<CourtTypeAtHour>>> iterator;

    Recipe(Map<Integer, List<CourtTypeAtHour>> map) {
        this.map = map;
        this.iterator = map.entrySet().iterator();
    }


    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public Map.Entry<Integer, List<CourtTypeAtHour>> next() {
        if (!hasNext()) throw new NoSuchElementException();

        return iterator.next();
    }

    public Integer nextWeight() {
        return next().getKey();
    }

    public List<CourtTypeAtHour> nextCourtTypeAtHour() {
        return next().getValue();

    }
}
