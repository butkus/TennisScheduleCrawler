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

    public List<Long> getCourtIds() {
        return map.values().stream()
                .flatMap(List::stream)
                .flatMap(e -> e.getCourtType().getIds().stream())       // fixme: getIds() here actually refer to `Court` type
                .map(Court::getCourtId)
                .distinct()
                .toList();
    }

    public List<Integer> getCourtTypeIds() {
        return map.values().stream()
                .flatMap(List::stream)
                .flatMap(e -> e.getCourtType().getIds().stream())   // fixme: (search for fooFix): `ids` are actually or type `Court`
                .map(Court::getCourtType)
                .distinct()
                .map(CourtType::getCourtTypeId)
                .toList();
    }

    public abstract List<Integer> getDurationPreference();
}
