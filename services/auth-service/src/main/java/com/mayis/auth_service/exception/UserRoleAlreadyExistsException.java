package com.mayis.auth_service.exception;

public class UserRoleAlreadyExistsException extends RuntimeException {

    public UserRoleAlreadyExistsException(String message) {
        super(message);
    }
}
