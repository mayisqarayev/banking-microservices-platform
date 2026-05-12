package com.mayis.auth_service.exception;

public class InvalidTokenTypeException extends RuntimeException {

    public InvalidTokenTypeException(String message) {
        super(message);
    }
}
