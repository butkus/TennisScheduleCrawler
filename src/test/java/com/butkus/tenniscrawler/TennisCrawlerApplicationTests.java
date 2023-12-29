package com.butkus.tenniscrawler;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

//@ExtendWith(SpringExtension.class)
@SpringBootTest
class TennisCrawlerApplicationTests {

	@Autowired
	private Crawler crawler;

	@Disabled("for one-time runs, create a run configuration with debugMode=true.")
	@Test
	void runNewSystem() {
		crawler.newSystem();
	}
}
