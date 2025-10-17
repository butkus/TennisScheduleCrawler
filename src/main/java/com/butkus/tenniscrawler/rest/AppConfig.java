package com.butkus.tenniscrawler.rest;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Configuration
public class AppConfig {

    private final boolean debugMode;

    public AppConfig(@Value("${app.debug-mode}") boolean debugMode) {
        this.debugMode = debugMode;
    }

    @Bean
    public RestTemplate restTemplate() {
        HttpClient client = HttpClients.createDefault();
        ClientHttpRequestFactory requestFactory = new BufferingClientHttpRequestFactory(
                new HttpComponentsClientHttpRequestFactory(client));
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        restTemplate.setInterceptors(Collections.singletonList(new OutgoingRequestLoggingInterceptor(debugMode)));
        return restTemplate;
    }
}
