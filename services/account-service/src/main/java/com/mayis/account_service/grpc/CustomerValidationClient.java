package com.mayis.account_service.grpc;

import com.mayis.account_service.exception.CustomerValidationException;
import com.mayis.account_service.exception.CustomerNotFoundException;
import com.mayis.customer_validation.grpc.CustomerValidationResponse;
import com.mayis.customer_validation.grpc.CustomerValidationServiceGrpc;
import com.mayis.customer_validation.grpc.GetCustomerValidationRequest;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CustomerValidationClient {

    @GrpcClient("customer-validation")
    private CustomerValidationServiceGrpc.CustomerValidationServiceBlockingStub customerValidationStub;

    public CustomerValidationResult validate(UUID customerId) {
        try {
            CustomerValidationResponse response = customerValidationStub.getCustomerValidation(
                    GetCustomerValidationRequest.newBuilder()
                            .setCustomerId(customerId.toString())
                            .build()
            );

            return new CustomerValidationResult(
                    UUID.fromString(response.getCustomerId()),
                    UUID.fromString(response.getUserId()),
                    response.getStatus(),
                    response.getDeleted()
            );
        } catch (StatusRuntimeException exception) {
            if (exception.getStatus().getCode() == Status.Code.NOT_FOUND) {
                throw new CustomerNotFoundException("Customer not found");
            }
            throw new CustomerValidationException("Customer validation failed: " + exception.getStatus().getDescription());
        }
    }

    public record CustomerValidationResult(
            UUID customerId,
            UUID userId,
            String status,
            boolean deleted
    ) {
    }
}
