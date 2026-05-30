package com.mayis.customer_service.exception;

public class CustomerAddressNotFoundException extends RuntimeException {

    public CustomerAddressNotFoundException(String message) {
        super(message);
    }
}
