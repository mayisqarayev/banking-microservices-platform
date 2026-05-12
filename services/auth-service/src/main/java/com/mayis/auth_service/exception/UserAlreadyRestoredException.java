package com.mayis.auth_service.exception;

public class UserAlreadyRestoredException extends RuntimeException {

    public UserAlreadyRestoredException(String message) {
        super(message);
    }
}
