package com.mayis.auth_service.exception;

public class UserAlreadySuspendedException extends RuntimeException {

    public UserAlreadySuspendedException(String message) {
        super(message);
    }
}
