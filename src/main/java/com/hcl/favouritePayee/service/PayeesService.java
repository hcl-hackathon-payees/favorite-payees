package com.hcl.favouritePayee.service;

import com.hcl.favouritePayee.dto.CreateFavoriteAccountRequest;
import com.hcl.favouritePayee.dto.FavoriteAccountResponse;
import com.hcl.favouritePayee.dto.FavoritePayeeResponse;
import com.hcl.favouritePayee.dto.UpdateFavoriteAccountRequest;
import com.hcl.favouritePayee.entity.BankCodeMapping;
import com.hcl.favouritePayee.entity.FavoritePayee;
import com.hcl.favouritePayee.exception.BankNotFoundException;
import com.hcl.favouritePayee.repository.BankCodeRepository;
import com.hcl.favouritePayee.repository.FavoritePayeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PayeesService {

    private final FavoritePayeeRepository repository;
    private final BankCodeRepository bankCodeRepository;

    public Page<FavoritePayeeResponse> getFavoriteAccounts(Long customerId, Pageable pageable) {
        return repository.findByCustomerId(customerId, pageable);
    }

    @Transactional(readOnly = true)
    public FavoriteAccountResponse getFavoriteAccount(Long customerId, Long id) {
        FavoritePayee payee = repository.findByIdAndCustomerId(id, customerId)
                .orElseThrow(() -> new RuntimeException("Favorite payee not found"));
        return toAccountResponse(payee);
    }

    @Transactional
    public FavoriteAccountResponse createFavoriteAccount(Long customerId, CreateFavoriteAccountRequest request) {
        String bankName = resolveBankFromIban(request.getIban());

        FavoritePayee payee = FavoritePayee.builder()
                .customerId(customerId)
                .accountName(request.getAccountName())
                .iban(request.getIban())
                .bankName(bankName)
                .build();

        return toAccountResponse(repository.save(payee));
    }

    @Transactional
    public FavoriteAccountResponse updateFavoriteAccount(Long customerId, Long id, UpdateFavoriteAccountRequest request) {
        FavoritePayee payee = repository.findByIdAndCustomerId(id, customerId)
                .orElseThrow(() -> new RuntimeException("Favorite payee not found"));

        if (request.getAccountName() != null) {
            payee.setAccountName(request.getAccountName());
        }
        if (request.getIban() != null) {
            payee.setIban(request.getIban());
            payee.setBankName(resolveBankFromIban(request.getIban()));
        }

        return toAccountResponse(repository.save(payee));
    }

    @Transactional
    public void deleteFavoriteAccount(Long customerId, Long id) {
        FavoritePayee payee = repository.findByIdAndCustomerId(id, customerId)
                .orElseThrow(() -> new RuntimeException("Favorite payee not found"));
        repository.delete(payee);
    }

    public String getBankFromIban(String iban) {
        String bankCode = extractBankCodeFromIban(iban);
        return resolveBankFromIban(bankCode);
    }

    public String extractBankCodeFromIban(String iban) {
        if (iban.length()>20 ) {
            throw new IllegalArgumentException("Invalid IBAN length");
        }
        return iban.substring(4, 8);
    }

    private String resolveBankFromIban(String iban) {
        String bankCode = extractBankCodeFromIban(iban);
        BankCodeMapping mapping = bankCodeRepository.findById(bankCode)
                .orElseThrow(() -> new BankNotFoundException(bankCode));
        return mapping.getBankName();
    }

//    private BankResolutionResponse resolveBankCode(String bankCode) {
//        BankCodeMapping mapping = bankCodeRepository.findById(bankCode)
//                .orElseThrow(() -> new BankNotFoundException(bankCode));
//        return BankResolutionResponse.builder()
//                .bankCode(mapping.getCode())
//                .bankName(mapping.getBankName())
//                .build();
//    }

    private FavoriteAccountResponse toAccountResponse(FavoritePayee payee) {
        return FavoriteAccountResponse.builder()
                .id(payee.getId())
                .customerId(payee.getCustomerId())
                .accountName(payee.getAccountName())
                .iban(payee.getIban())
                .bankName(payee.getBankName())
                .createdAt(payee.getCreatedAt())
                .updatedAt(payee.getUpdatedAt())
                .build();
    }
}