package com.mayis.customer_service.event;

public interface CustomerEventPublisher {

    void publishCustomerCreated(CustomerCreatedEvent event);

    void publishCustomerUpdated(CustomerUpdatedEvent event);

    void publishCustomerBlocked(CustomerBlockedEvent event);
}
