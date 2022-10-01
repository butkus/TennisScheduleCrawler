package com.butkus.tenniscrawler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

//@ExtendWith(SpringExtension.class)
@SpringBootTest
class TennisCrawlerApplicationTests {

	@Autowired
	private Crawler crawler;

	@Test
	void contextLoads() {
		try {
			crawler.run();
		} catch (InterruptedException e) {
			System.out.println(" --- CRAWLER TEST FAILED --- ");
			throw new RuntimeException(e);
		}
	}

}
