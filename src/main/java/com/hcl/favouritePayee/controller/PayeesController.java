package com.hcl.favouritePayee.controller;

import com.hcl.favouritePayee.dto.FavoriteAccountResponse;
import com.hcl.favouritePayee.dto.UpdateFavoriteAccountRequest;
import com.hcl.favouritePayee.service.PayeesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PayeesController {

    private final PayeesService payeesService;

    @GetMapping("/payee/{customerId}/favorite-accounts")
    public ResponseEntity<Page<FavoriteAccountResponse>> getFavoritePayees(
            @PathVariable Long customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<FavoriteAccountResponse> accounts = payeesService.getFavoriteAccounts(customerId, pageable);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/payee/{id}")
    public ResponseEntity<FavoriteAccountResponse> getFavoritePayeeById(
            @PathVariable Long customerId,
            @PathVariable Long id) {
        FavoriteAccountResponse account = payeesService.getFavoriteAccount(customerId, id);
        return ResponseEntity.ok(account);
    }

    @PostMapping("/payee")
    public ResponseEntity<FavoriteAccountResponse> createFavoritePayee(
            @PathVariable Long customerId,
            @Valid @RequestBody CreateFavoriteAccountRequest request) {
        FavoriteAccountResponse account = payeesService.createFavoriteAccount(customerId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(account);
    }

    @PutMapping("/payee/{id}")
    public ResponseEntity<FavoriteAccountResponse> updateFavoritePayee(
            @PathVariable Long customerId,
            @PathVariable Long id,
            @RequestBody UpdateFavoriteAccountRequest request) {
        FavoriteAccountResponse account = payeesService.updateFavoriteAccount(customerId, id, request);
        return ResponseEntity.ok(account);
    }

    @DeleteMapping("/payee/{id}")
    public ResponseEntity<Void> deleteFavoritePayees(
            @PathVariable Long customerId,
            @PathVariable Long id) {
        payeesService.deleteFavoriteAccount(customerId, id);
        return ResponseEntity.noContent().build();
    }
}