package com.butkus.tenniscrawler;

public class OrderWithoutDesireException extends RuntimeException {

    public OrderWithoutDesireException() {
    }

    public OrderWithoutDesireException(String message) {
        super(message);
    }
}
