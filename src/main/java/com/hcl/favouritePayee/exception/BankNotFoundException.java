package com.hcl.favouritePayee.exception;

import lombok.Getter;

@Getter
public class BankNotFoundException extends RuntimeException {

    private final String bankCode;

    public BankNotFoundException(String bankCode) {
        super("Bank not found for code: " + bankCode);
        this.bankCode = bankCode;
    }
}