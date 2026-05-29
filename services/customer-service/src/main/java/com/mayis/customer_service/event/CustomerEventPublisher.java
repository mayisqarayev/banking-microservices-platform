package com.mayis.customer_service.event;

public interface CustomerEventPublisher {

    void publishCustomerCreated(CustomerCreatedEvent event);
}
