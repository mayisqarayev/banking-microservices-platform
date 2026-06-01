package com.mayis.customer_service.grpc;

import com.mayis.customer_service.model.entity.Customer;
import com.mayis.customer_service.repository.CustomerRepository;
import com.mayis.customer_validation.grpc.CustomerValidationResponse;
import com.mayis.customer_validation.grpc.CustomerValidationServiceGrpc;
import com.mayis.customer_validation.grpc.GetCustomerValidationRequest;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.UUID;

@GrpcService
public class CustomerValidationGrpcService extends CustomerValidationServiceGrpc.CustomerValidationServiceImplBase {

    private final CustomerRepository customerRepository;

    public CustomerValidationGrpcService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public void getCustomerValidation(
            GetCustomerValidationRequest request,
            StreamObserver<CustomerValidationResponse> responseObserver
    ) {
        try {
            UUID customerId = UUID.fromString(request.getCustomerId());
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> Status.NOT_FOUND
                            .withDescription("Customer not found")
                            .asRuntimeException());

            CustomerValidationResponse response = CustomerValidationResponse.newBuilder()
                    .setCustomerId(customer.getId().toString())
                    .setUserId(customer.getUserId().toString())
                    .setStatus(customer.getStatus().name())
                    .setDeleted(customer.isDeleted())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (IllegalArgumentException exception) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Invalid customerId")
                    .asRuntimeException());
        } catch (RuntimeException exception) {
            responseObserver.onError(exception);
        }
    }
}
