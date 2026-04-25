package com.hcl.favouritePayee.service;

import com.hcl.favouritePayee.dto.CreateFavoriteAccountRequest;
import com.hcl.favouritePayee.dto.FavoritePayeeResponse;
import com.hcl.favouritePayee.entity.BankCodeMapping;
import com.hcl.favouritePayee.entity.FavoritePayee;
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

    @Transactional
    public FavoritePayeeResponse createFavoriteAccount(Long customerId, CreateFavoriteAccountRequest request) {
        String bankName = resolveBankFromIban(request.getIban());

        FavoritePayee account = FavoritePayee.builder()
                .customerId(customerId)
                .accountName(request.getAccountName())
                .iban(request.getIban())
                .bankName(bankName)
                .build();

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

    private FavoritePayeeResponse toResponse(FavoritePayee account) {
        return FavoritePayeeResponse.builder()
                .id(account.getId())
                .customerId(account.getCustomerId())
                .accountName(account.getAccountName())
                .iban(account.getIban())
                .bankName(account.getBankName())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }

}
