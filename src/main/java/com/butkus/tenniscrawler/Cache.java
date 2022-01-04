package com.butkus.tenniscrawler;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class Cache {

    private Instant lastUpdated;
    private final Duration updateFrequency;
    private Map<Pair<LocalDate, Integer>, List<Integer>> map;   // Date+Court --> aggregatedCourts
    private final List<Integer> cacheableColors = List.of(Colors.ORANGE, Colors.GREEN, Colors.YELLOW);

    @Autowired
    public Cache(@Value("${app.cache-update-frequency}") Duration updateFrequency) {
        this.lastUpdated = Instant.MIN;
        this.updateFrequency = updateFrequency;
        this.map = new HashMap<>();
    }

    public boolean isStale() {
        Instant freshUntil = lastUpdated.plus(updateFrequency);
        return freshUntil.isBefore(Instant.now());
    }

    public Duration durationToLive() {
        Instant freshUntil = lastUpdated.plus(updateFrequency);
        return Duration.between(Instant.now(), freshUntil);
    }

    public void clearCache() {
        map.clear();
    }

    public void addIfCacheable(LocalDate date, Integer courtId, List<Integer> aggregatedCourts) {
        if (cacheable(aggregatedCourts)) {
            map.put(Pair.with(date, courtId), aggregatedCourts);
        }
    }

    private boolean cacheable(List<Integer> aggregatedCourts) {
        return aggregatedCourts.stream().anyMatch(cacheableColors::contains);
    }

    public void setUpdated() {
        lastUpdated = Instant.now();
    }

    public List<Integer> get(Pair<LocalDate, Integer> with) {
        return map.get(with);
    }
}
