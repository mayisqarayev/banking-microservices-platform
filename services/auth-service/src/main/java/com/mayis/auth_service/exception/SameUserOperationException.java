package com.mayis.auth_service.exception;

public class SameUserOperationException extends RuntimeException {

    public SameUserOperationException(String message) {
        super(message);
    }
}
