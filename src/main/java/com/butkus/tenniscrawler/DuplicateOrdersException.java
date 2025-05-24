package com.butkus.tenniscrawler;

public class DuplicateOrdersException extends RuntimeException {

    public DuplicateOrdersException() {
    }

    public DuplicateOrdersException(String message) {
        super(message);
    }
}
