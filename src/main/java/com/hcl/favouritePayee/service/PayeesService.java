package com.hcl.favouritePayee.service;

import com.hcl.favouritePayee.dto.CreateFavoriteAccountRequest;
import com.hcl.favouritePayee.dto.FavoritePayeeResponse;
import com.hcl.favouritePayee.dto.UpdateFavoriteAccountRequest;
import com.hcl.favouritePayee.entity.BankCodeMapping;
import com.hcl.favouritePayee.entity.Customer;
import com.hcl.favouritePayee.entity.FavoritePayee;
import com.hcl.favouritePayee.exception.ResourceNotFoundException;
import com.hcl.favouritePayee.repository.BankCodeRepository;
import com.hcl.favouritePayee.repository.CustomerRepository;
import com.hcl.favouritePayee.repository.FavoritePayeeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PayeesService {

    private final FavoritePayeeRepository repository;
    private final BankCodeRepository bankCodeRepository;
    private final CustomerRepository customerRepository;

    public Page<FavoritePayee> getFavoriteAccounts(Long customerId, Pageable pageable) {
        return  repository.findByCustomerId(customerId, pageable);

    }

    @Transactional
    public FavoritePayeeResponse createFavoriteAccount(Long customerId, CreateFavoriteAccountRequest request) {

        // Step 1: Fetch customer from DB
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // Step 2: Resolve bank
        String bankName = resolveBankFromIban(request.getIban());

        // Step 3: Build entity
        FavoritePayee account = FavoritePayee.builder()
                .customer(customer)
                .accountName(request.getAccountName())
                .iban(request.getIban())
                .bankName(bankName)
                .build();

        // Step 4: Save
        return toResponse(repository.save(account));
    }
    private String resolveBankFromIban(String iban) {
        if (iban == null || iban.length() < 8) {
            return "Unknown Bank";
        }

        String bankCode = iban.substring(4, 8);
        return bankCodeRepository.findById(bankCode)
                .map(BankCodeMapping::getBankName)
                .orElse("Unknown Bank");
    }

    public FavoritePayeeResponse getFavoriteAccount(Long id) {
        FavoritePayee account = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Favorite payee not found"));
        return toResponse(account);
    }

    @Transactional
    public FavoritePayeeResponse updateFavoriteAccount(Long id,
                                                       UpdateFavoriteAccountRequest request) {

        FavoritePayee account = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Favorite payee not found"
                ));

        if (request.getAccountName() != null) {
            account.setAccountName(request.getAccountName());
        }

        if (request.getIban() != null) {
            account.setIban(request.getIban());
            account.setBankName(resolveBankFromIban(request.getIban()));
        }

        return toResponse(repository.save(account));
    }

    @Transactional
    public void deleteFavoriteAccount(Long id) {

        FavoritePayee account = repository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Favorite account not found with id "));

        repository.delete(account);
    }

    private FavoritePayeeResponse toResponse(FavoritePayee account) {
        return FavoritePayeeResponse.builder()
                .id(account.getId())
                .customerId(account.getCustomer().getId())
                .accountName(account.getAccountName())
                .iban(account.getIban())
                .bankName(account.getBankName())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }

}