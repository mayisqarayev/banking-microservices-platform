package com.mayis.customer_service.exception;

public class CustomerAlreadyDeletedException extends RuntimeException {

    public CustomerAlreadyDeletedException(String message) {
        super(message);
    }
}
