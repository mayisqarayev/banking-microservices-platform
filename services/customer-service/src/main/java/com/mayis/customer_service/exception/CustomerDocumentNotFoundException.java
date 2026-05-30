package com.mayis.customer_service.exception;

public class CustomerDocumentNotFoundException extends RuntimeException {

    public CustomerDocumentNotFoundException(String message) {
        super(message);
    }
}
