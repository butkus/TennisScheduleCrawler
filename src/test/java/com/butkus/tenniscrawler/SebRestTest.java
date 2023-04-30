package com.butkus.tenniscrawler;

import com.butkus.tenniscrawler.rest.Seb;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class SebRestTest {

    @Autowired
    Seb rt;

    @Test
    void name() {
        String s = rt.callSeb();
        System.out.println("--- s = " + s);
        assertNotNull(s);
    }
}