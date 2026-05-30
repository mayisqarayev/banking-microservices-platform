package com.mayis.customer_service.exception;

public class InvalidCustomerAddressException extends RuntimeException {

    public InvalidCustomerAddressException(String message) {
        super(message);
    }
}
