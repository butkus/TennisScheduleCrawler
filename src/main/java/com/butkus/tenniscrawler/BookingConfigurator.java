package com.butkus.tenniscrawler;

import com.butkus.tenniscrawler.rest.SebFetcher;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Getter
@Component
public class BookingConfigurator {

    // todo
//        bookingConfigurator.setEarliestWeekday(...).setLatestWeekday(...).setEarliestsWeekend(...).setLatestWeekend(...)
//        .setPreferencePrioritiesWeekday(...).setPreferencePrioritiesWeekend(...)
//        .setEarlierLaterCourtTypeTradeIn(...)
//        .build(collect);  // collect = inputs

    public static final LocalTime EARLY_BIRD = LocalTime.parse("18:00");
    public static final LocalTime COMFORTABLE = LocalTime.parse("18:30");
    public static final LocalTime LATE_OWL = LocalTime.parse("19:30");

    @Autowired
    public BookingConfigurator(AudioPlayer audioPlayer, SebFetcher fetcher) {
        this.audioPlayer = audioPlayer;
        this.fetcher = fetcher;
    }

    private final AudioPlayer audioPlayer;
    @Getter private final SebFetcher fetcher;

    public void resetAudioPlayer() {
        this.audioPlayer.reset();
    }

}
