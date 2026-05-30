package com.mayis.customer_service.exception;

public class InvalidCustomerStateException extends RuntimeException {

    public InvalidCustomerStateException(String message) {
        super(message);
    }
}
