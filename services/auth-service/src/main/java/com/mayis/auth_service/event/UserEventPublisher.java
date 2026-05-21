package com.mayis.auth_service.event;

public interface UserEventPublisher {

    void publishUserRegistered(UserRegisteredEvent event);

    void publishUserRoleAssigned(UserRoleAssignedEvent event);

    void publishUserRoleRemoved(UserRoleRemovedEvent event);

    void publishUserDeleted(UserDeletedEvent event);

    void publishUserSuspended(UserSuspendedEvent event);

    void publishUserRestored(UserRestoredEvent event);

    void publishUserActivated(UserActivatedEvent event);

    void publishUserDeactivated(UserDeactivatedEvent event);

    void publishUserUnsuspended(UserUnsuspendedEvent event);
}
