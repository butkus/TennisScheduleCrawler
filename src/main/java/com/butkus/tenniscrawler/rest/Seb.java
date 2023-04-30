package com.butkus.tenniscrawler.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;

@Service
public class Seb {

    private final RestTemplate restTemplate;

    @Autowired
    public Seb(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String callSeb() {
        String url = "https://ws.tenisopasaulis.lt/api/v1/timeInfoBatch";
        URI uri = URI.create(url);
        String payload = "{\"time\":\"19:30\",\"courts\":[1,8,10,11,12,13,14,15,16,17,18,19,20,21,44,45,46,47,48,49,50,51,52,53,54,55,59,61,63,65,67,69,135,137,139,141,143,145,147],\"date\":\"2023-05-02\",\"salePoint\":11,\"sessionToken\":\"93b25e67d079e4871686c18b02fe62f9\"}";
        List<Long> courts = List.of(1L,7L,8L,10L,11L,12L,13L,14L,15L,16L,17L,18L,19L,20L,21L,44L,45L,46L,47L,48L,49L,50L,51L,52L,53L,54L,55L,59L,61L,63L,65L,67L,69L,135L,137L,139L,141L,143L,145L,147L);
        AvailableCourtsRqstDto rqstDto = new AvailableCourtsRqstDto()
                .setTime("19:30")
                .setCourts(courts)
                .setDate("2023-04-29")
                .setSalePoint(11L)
                .setSessionToken("93b25e67d079e4871686c18b02fe62f9");

        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.add("Accept", "application/json, text/plain, */*");
        headers.add("Accept-Encoding", "gzip, deflate, br");
        headers.add("Content-Type", "application/json");

        RequestEntity<AvailableCourtsRqstDto> requestEntity = new RequestEntity<>(rqstDto, headers, HttpMethod.POST, uri);
        ResponseEntity<AvailableCourtsRspDto> response = restTemplate.exchange(requestEntity, new ParameterizedTypeReference<AvailableCourtsRspDto>() {});

        AvailableCourtsRspDto body = response.getBody();
        return body.toString();
    }

}
