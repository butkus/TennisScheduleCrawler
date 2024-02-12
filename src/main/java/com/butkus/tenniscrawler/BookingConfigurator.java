package com.butkus.tenniscrawler;

import com.butkus.tenniscrawler.rest.SebFetcher;
import lombok.Data;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
