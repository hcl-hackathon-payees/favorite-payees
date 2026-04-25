package com.hcl.favouritePayee.service;

import com.hcl.favouritePayee.dto.CreateFavoriteAccountRequest;
import com.hcl.favouritePayee.dto.FavoritePayeeResponse;
import com.hcl.favouritePayee.dto.UpdateFavoriteAccountRequest;
import com.hcl.favouritePayee.entity.BankCodeMapping;
import com.hcl.favouritePayee.entity.FavoritePayee;
import com.hcl.favouritePayee.repository.BankCodeRepository;
import com.hcl.favouritePayee.repository.FavoritePayeeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
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

    public FavoritePayeeResponse getFavoriteAccount(Long customerId, Long id) {
        FavoritePayee account = repository.findByIdAndCustomerId(id, customerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Favorite payee not found"));
        return toResponse(account);
    }

    @Transactional
    public FavoritePayeeResponse updateFavoriteAccount(Long customerId, Long id, UpdateFavoriteAccountRequest request) {
        throw new UnsupportedOperationException("Update payee is not implemented");
    }

    @Transactional
    public void deleteFavoriteAccount(Long customerId, Long id) {

        FavoriteAccount account = favoritePayeeRepository
                .findByIdAndCustomerId(id, customerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Favorite account not found with id " + id + " for customer " + customerId));

        favoritePayeeRepository.delete(account);
    }

    private FavoritePayeeResponse toResponse(FavoriteAccount account) {
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
