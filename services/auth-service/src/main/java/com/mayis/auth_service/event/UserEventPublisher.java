package com.mayis.auth_service.event;

public interface UserEventPublisher {

    void publishUserRegistered(UserRegisteredEvent event);
}
