package com.butkus.tenniscrawler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TennisCrawlerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TennisCrawlerApplication.class, args);
	}

}
