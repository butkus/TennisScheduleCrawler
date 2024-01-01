package com.butkus.tenniscrawler.rest;

import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
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
        org.apache.http.client.HttpClient client = HttpClients.custom().build();
        ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(client);
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder().requestFactory(
                () -> new BufferingClientHttpRequestFactory(requestFactory));
        RestTemplate restTemplate = restTemplateBuilder.build();
        restTemplate.setInterceptors(Collections.singletonList(new OutgoingRequestLoggingInterceptor(debugMode)));
        return restTemplate;
    }
}
