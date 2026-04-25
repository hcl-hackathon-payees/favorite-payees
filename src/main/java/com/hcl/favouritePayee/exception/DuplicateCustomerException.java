package com.hcl.favouritePayee.exception;

import lombok.Getter;

@Getter
public class DuplicateCustomerException extends RuntimeException {

    private final Long customerId;

    public DuplicateCustomerException(Long customerId) {
        super("Customer with id " + customerId + " already exists");
        this.customerId = customerId;
    }
}