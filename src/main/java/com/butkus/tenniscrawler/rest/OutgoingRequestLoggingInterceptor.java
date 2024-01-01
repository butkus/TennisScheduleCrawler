package com.butkus.tenniscrawler.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class OutgoingRequestLoggingInterceptor implements ClientHttpRequestInterceptor {

    private final boolean debugMode;

    public OutgoingRequestLoggingInterceptor(boolean debugMode) {
        this.debugMode = debugMode;
    }

    private static final Logger log = LoggerFactory.getLogger("com.butkus.tenniscrawler.RequestResponse");

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        HttpMethod method = request.getMethod();
        URI uri = request.getURI();
        HttpHeaders headers = request.getHeaders();
        String requestBody = new String(body, StandardCharsets.UTF_8);
        if (debugMode) {
            log.info("http-request sent\nRequest: {} {}\nHeaders: {}\nBody: {}\n", method, uri, headers, requestBody);
        }

        long startTime = System.currentTimeMillis();
        ClientHttpResponse response = execution.execute(request, body);

        long duration = System.currentTimeMillis() - startTime;

        String reason = null;
        try {
            reason = response.getStatusCode().getReasonPhrase();
        } catch (IOException e) {
            reason = "";
        }

        String bodyString = null;
        try {
            bodyString = StreamUtils.copyToString(response.getBody(), Charset.defaultCharset());
        } catch (IOException e) {
            log.warn("Failed to read response body content.", e);
        }

        if (debugMode) {
            log.info("http-response received (duration: {} ms)\nStatus: {} {}\nHeaders: {}\nBody: {}\n",
                    duration, response.getRawStatusCode(), reason, response.getHeaders(), bodyString);
        }

        return response;
    }
}
