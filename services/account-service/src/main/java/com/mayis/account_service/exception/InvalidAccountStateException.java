package com.mayis.account_service.exception;

public class InvalidAccountStateException extends RuntimeException {

    public InvalidAccountStateException(String message) {
        super(message);
    }
}
