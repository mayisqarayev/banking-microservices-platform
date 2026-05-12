package com.mayis.auth_service.exception;

public class UserAlreadyUnlockedException extends RuntimeException {

    public UserAlreadyUnlockedException(String message) {
        super(message);
    }
}
