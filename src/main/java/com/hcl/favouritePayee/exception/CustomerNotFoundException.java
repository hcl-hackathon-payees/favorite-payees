package com.hcl.favouritePayee.exception;

import lombok.Getter;

@Getter
public class CustomerNotFoundException extends RuntimeException {

    private final Long customerId;

    public CustomerNotFoundException(Long customerId) {
        super("Customer not found with id: " + customerId);
        this.customerId = customerId;
    }
}