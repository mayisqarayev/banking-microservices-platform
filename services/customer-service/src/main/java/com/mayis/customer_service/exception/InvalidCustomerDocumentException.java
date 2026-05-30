package com.mayis.customer_service.exception;

public class InvalidCustomerDocumentException extends RuntimeException {

    public InvalidCustomerDocumentException(String message) {
        super(message);
    }
}
