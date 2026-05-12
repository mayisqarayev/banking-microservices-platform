package com.mayis.auth_service.exception;

public class InvalidUserStateException extends RuntimeException {

    public InvalidUserStateException(String message) {
        super(message);
    }
}
