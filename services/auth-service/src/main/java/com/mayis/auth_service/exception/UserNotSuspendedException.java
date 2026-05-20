package com.mayis.auth_service.exception;

public class UserNotSuspendedException extends RuntimeException {

    public UserNotSuspendedException(String message) {
        super(message);
    }
}
