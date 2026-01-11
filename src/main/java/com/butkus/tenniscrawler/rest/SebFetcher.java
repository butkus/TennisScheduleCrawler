package com.butkus.tenniscrawler.rest;

import com.butkus.tenniscrawler.rest.orders.OrdersRspDto;
import com.butkus.tenniscrawler.rest.placeinfobatch.PlaceInfoBatchRqstDto;
import com.butkus.tenniscrawler.rest.placeinfobatch.PlaceInfoBatchRspDto;
import com.butkus.tenniscrawler.rest.timeinfobatch.TimeInfoBatchRqstDto;
import com.butkus.tenniscrawler.rest.timeinfobatch.TimeInfoBatchRspDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Component
public class SebFetcher {

    public final String sessionToken;
    private final RestTemplate restTemplate;
    private final boolean debugMode;

    @Autowired
    public SebFetcher(RestTemplate restTemplate,
                      @Value("${app.debug-mode}") boolean debugMode,
                      @Value("${app.session-token}") String sessionToken) {
        this.restTemplate = restTemplate;
        this.debugMode = debugMode;
        this.sessionToken = sessionToken;
    }

    // can be retrieved via GET https://ws.tenisopasaulis.lt/api/v1/allPlacesInfo
    // Vidaus hard          placeID=2
    // Kilimas              placeID=8
    // Vidaus pletra        placeID=18
    // Bernardinai gruntas  placeID=5
    // Bernardinai zole     placeID=20

    // 1-st rqst in webpage
    public String checkToken() {
        String url = "https://ws.tenisopasaulis.lt/api/v1/checkToken";

        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.add("Accept", "application/json, text/plain, */*");
        headers.add("Accept-Encoding", "gzip, deflate, br");
        headers.add("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundarypHEpWrdQcXY7Io6N");
        // todo: pass this as form-data: session_token: SESSION_TOKEN

        return null;
    }

    // todo this is a pre-flight request, don't think I need it
    public HttpStatusCode optionsPlaceInfoBatch() {
        String url = "https://ws.tenisopasaulis.lt/api/v1/placeInfoBatch";
        URI uri = URI.create(url);
        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.add("Accept", "*/*");
        headers.add("Accept-Encoding", "gzip, deflate, br");
        headers.add("Access-Control-Request-Headers", "content-type");
        headers.add("Access-Control-Request-Method", "POST");

        RequestEntity<Void> requestEntity = new RequestEntity<>(null, headers, HttpMethod.OPTIONS, uri);
        ResponseEntity<String> response = restTemplate.exchange(requestEntity, new ParameterizedTypeReference<>() {
        });
        return response.getStatusCode();
    }

    /*
    Provides tens-of-thousands-lines-long json with individual court availability on half-hourly basis
    Used in SEB UI to make calendar view with bubbles indicating how many courts are available for any given half-hour
     */
    public PlaceInfoBatchRspDto postPlaceInfoBatch(List<String> dates, List<Integer> places) {
        String url = "https://ws.tenisopasaulis.lt/api/v1/placeInfoBatch";
        URI uri = URI.create(url);

        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.add("Accept", "application/json, text/plain, */*");
        headers.add("Accept-Encoding", "gzip, deflate, br");
        headers.add("Content-Type", "application/json");

        PlaceInfoBatchRqstDto request = new PlaceInfoBatchRqstDto()
                .setExcludeCourtName(Boolean.TRUE)
                .setExcludeInfoUrl(Boolean.TRUE)
                .setPlaces(places)
                .setDates(dates)
                .setSalePoint(11L)
                .setSessionToken(sessionToken);

        RequestEntity<PlaceInfoBatchRqstDto> requestEntity = new RequestEntity<>(request, headers, HttpMethod.POST, uri);
        ResponseEntity<PlaceInfoBatchRspDto> response = restTemplate.exchange(requestEntity, new ParameterizedTypeReference<>() {
        });

        return response.getBody();
    }

    /*
    Provides court availability with granularity. Can provide desired court(s), time, date.
    Responses include 30-60-90-120 min. availability info.
     */
    public TimeInfoBatchRspDto postTimeInfoBatch(List<Long> courts, LocalDate date, LocalTime time) {
        if (debugMode) {
            System.out.println("--- ==== SEARCHING FOR  " + date + "  " + time + " ==== ---");
        }

        String url = "https://ws.tenisopasaulis.lt/api/v1/timeInfoBatch";
        URI uri = URI.create(url);
        TimeInfoBatchRqstDto rqstDto = new TimeInfoBatchRqstDto()
                .setTime(time.toString())
                .setCourts(courts)
                .setDate(date.toString())
                .setSalePoint(11L)
                .setSessionToken(sessionToken);

        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.add("Accept", "application/json, text/plain, */*");
        headers.add("Accept-Encoding", "gzip, deflate, br");
        headers.add("Content-Type", "application/json");

        RequestEntity<TimeInfoBatchRqstDto> requestEntity = new RequestEntity<>(rqstDto, headers, HttpMethod.POST, uri);
        ResponseEntity<TimeInfoBatchRspDto> response = restTemplate.exchange(requestEntity, new ParameterizedTypeReference<>() {
        });

        return response.getBody();
    }

    public OrdersRspDto getOrders(String from, String to) {
        String url = String.format("https://ws.tenisopasaulis.lt/api/v1/orders?sessionToken=%s&from=%s&to=%s", sessionToken, from, to);
        URI uri = URI.create(url);

        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.add("Accept", "application/json, text/plain, */*");
        headers.add("Accept-Encoding", "gzip, deflate, br");

        RequestEntity<Void> requestEntity = new RequestEntity<>(null, headers, HttpMethod.GET, uri);
        ResponseEntity<OrdersRspDto> response = restTemplate.exchange(requestEntity, new ParameterizedTypeReference<>() {
        });

        return response.getBody();
    }

}
