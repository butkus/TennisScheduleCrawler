package com.butkus.tenniscrawler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Component
public class Crawler {

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Scheduled(cron = "0/2 * * * * *")
    public void run(){
        System.out.println("-- Started at " + LocalTime.now().format(DATE_TIME_FORMATTER) );
    }

}
