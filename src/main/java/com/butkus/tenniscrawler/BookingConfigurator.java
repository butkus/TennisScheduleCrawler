package com.butkus.tenniscrawler;

import com.butkus.tenniscrawler.rest.SebFetcher;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalTime;

@Data
@Component
public class BookingConfigurator {

    // todo
//        bookingConfigurator.setEarliestWeekday(...).setLatestWeekday(...).setEarliestsWeekend(...).setLatestWeekend(...)
//        .setPreferencePrioritiesWeekday(...).setPreferencePrioritiesWeekend(...)
//        .setEarlierLaterCourtTypeTradeIn(...)
//        .build(collect);  // collect = inputs

    private LocalTime earlyBird = LocalTime.parse("18:00");
    private LocalTime comfortable = LocalTime.parse("18:30");
    private LocalTime lateOwl = LocalTime.parse("19:30");

    private final AudioPlayer audioPlayer;
    private final SebFetcher fetcher;
    private final Clock clock;


    @Autowired
    public BookingConfigurator(AudioPlayer audioPlayer, SebFetcher fetcher, Clock clock) {
        this.audioPlayer = audioPlayer;
        this.fetcher = fetcher;
        this.clock = clock;
    }

    public void resetAudioPlayer() {
        this.audioPlayer.reset();
    }

}
