package com.mayis.account_service.exception;

public class CustomerValidationException extends RuntimeException {

    public CustomerValidationException(String message) {
        super(message);
    }
}
