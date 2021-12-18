package com.butkus.tenniscrawler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TenniscrawlerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TenniscrawlerApplication.class, args);
	}

}
