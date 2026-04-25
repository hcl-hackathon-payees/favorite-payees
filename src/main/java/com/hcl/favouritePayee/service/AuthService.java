package com.hcl.favouritePayee.service;

import com.hcl.favouritePayee.dto.LoginResponse;
import com.hcl.favouritePayee.dto.RegisterRequest;
import com.hcl.favouritePayee.entity.Customer;
import com.hcl.favouritePayee.exception.CustomerNotFoundException;
import com.hcl.favouritePayee.exception.DuplicateCustomerException;
import com.hcl.favouritePayee.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final CustomerRepository customerRepository;

    public LoginResponse validateCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));

        return LoginResponse.builder()
                .customerId(customer.getId())
                .name(customer.getName())
                .message("Customer validated successfully")
                .build();
    }

    public LoginResponse registerCustomer(RegisterRequest request) {
        if (customerRepository.existsById(request.getCustomerId())) {
            throw new DuplicateCustomerException(request.getCustomerId());
        }

        Customer customer = Customer.builder()
                .id(request.getCustomerId())
                .name(request.getName())
                .build();

        customerRepository.save(customer);

        return LoginResponse.builder()
                .customerId(customer.getId())
                .name(customer.getName())
                .message("Customer registered successfully")
                .build();
    }
}