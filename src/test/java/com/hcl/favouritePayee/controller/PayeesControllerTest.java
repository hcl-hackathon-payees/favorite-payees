package com.hcl.favouritePayee.controller;

import com.hcl.favouritePayee.dto.FavoritePayeeResponse;
import com.hcl.favouritePayee.service.PayeesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PayeesControllerTest {

    @Mock
    private PayeesService payeesService;

    @InjectMocks
    private PayeesController payeesController;

    private FavoritePayeeResponse samplePayeeResponse;
    private Long customerId;
    private int page;
    private int size;

    @BeforeEach
    void setUp() {
        customerId = 1L;
        page = 0;
        size = 20;

        samplePayeeResponse = FavoritePayeeResponse.builder()
                .id(1L)
                .customerId(customerId)
                .accountName("John Doe")
                .iban("DE89370400440532013000")
                .bankName("Test Bank")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should return paginated favorite payees for valid customer")
    void getFavoritePayees_ShouldReturnPaginatedPayees() {
        Page<FavoritePayeeResponse> mockPage = new PageImpl<>(List.of(samplePayeeResponse), PageRequest.of(page, size), 1);
        when(payeesService.getFavoriteAccounts(eq(customerId), any(Pageable.class))).thenReturn(mockPage);

        ResponseEntity<Page<FavoritePayeeResponse>> response = payeesController.getFavoritePayees(customerId, page, size);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getContent()).hasSize(1);
        assertThat(response.getBody().getContent().get(0).getAccountName()).isEqualTo("John Doe");
        verify(payeesService).getFavoriteAccounts(eq(customerId), any(Pageable.class));
    }

    @Test
    @DisplayName("Should return empty page when no payees exist")
    void getFavoritePayees_ShouldReturnEmptyPage_WhenNoPayeesExist() {
        Page<FavoritePayeeResponse> emptyPage = new PageImpl<>(List.of(), PageRequest.of(page, size), 0);
        when(payeesService.getFavoriteAccounts(eq(customerId), any(Pageable.class))).thenReturn(emptyPage);

        ResponseEntity<Page<FavoritePayeeResponse>> response = payeesController.getFavoritePayees(customerId, page, size);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getContent()).isEmpty();
        assertThat(response.getBody().getTotalElements()).isZero();
    }

    @Test
    @DisplayName("Should return multiple payees in page")
    void getFavoritePayees_ShouldReturnMultiplePayees() {
        FavoritePayeeResponse payee2 = FavoritePayeeResponse.builder()
                .id(2L)
                .customerId(customerId)
                .accountName("Jane Doe")
                .iban("GB82WEST12345698765432")
                .bankName("West Bank")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Page<FavoritePayeeResponse> mockPage = new PageImpl<>(List.of(samplePayeeResponse, payee2), PageRequest.of(page, size), 2);
        when(payeesService.getFavoriteAccounts(eq(customerId), any(Pageable.class))).thenReturn(mockPage);

        ResponseEntity<Page<FavoritePayeeResponse>> response = payeesController.getFavoritePayees(customerId, page, size);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getContent()).hasSize(2);
        assertThat(response.getBody().getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should handle different page sizes correctly")
    void getFavoritePayees_ShouldHandleDifferentPageSizes() {
        int customSize = 10;
        Page<FavoritePayeeResponse> mockPage = new PageImpl<>(List.of(samplePayeeResponse), PageRequest.of(page, customSize), 1);
        when(payeesService.getFavoriteAccounts(eq(customerId), any(Pageable.class))).thenReturn(mockPage);

        ResponseEntity<Page<FavoritePayeeResponse>> response = payeesController.getFavoritePayees(customerId, page, customSize);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        verify(payeesService).getFavoriteAccounts(eq(customerId), any(Pageable.class));
    }

    @Test
    @DisplayName("Should handle different page numbers correctly")
    void getFavoritePayees_ShouldHandleDifferentPageNumbers() {
        int pageNumber = 2;
        Page<FavoritePayeeResponse> mockPage = new PageImpl<>(List.of(), PageRequest.of(pageNumber, size), 0);
        when(payeesService.getFavoriteAccounts(eq(customerId), any(Pageable.class))).thenReturn(mockPage);

        ResponseEntity<Page<FavoritePayeeResponse>> response = payeesController.getFavoritePayees(customerId, pageNumber, size);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(payeesService).getFavoriteAccounts(eq(customerId), any(Pageable.class));
    }

    @Test
    @DisplayName("Should return payee with correct account details")
    void getFavoritePayees_ShouldReturnPayeeWithCorrectDetails() {
        Page<FavoritePayeeResponse> mockPage = new PageImpl<>(List.of(samplePayeeResponse), PageRequest.of(page, size), 1);
        when(payeesService.getFavoriteAccounts(eq(customerId), any(Pageable.class))).thenReturn(mockPage);

        ResponseEntity<Page<FavoritePayeeResponse>> response = payeesController.getFavoritePayees(customerId, page, size);
        FavoritePayeeResponse result = response.getBody().getContent().get(0);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getCustomerId()).isEqualTo(customerId);
        assertThat(result.getAccountName()).isEqualTo("John Doe");
        assertThat(result.getIban()).isEqualTo("DE89370400440532013000");
        assertThat(result.getBankName()).isEqualTo("Test Bank");
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getUpdatedAt()).isNotNull();
    }
}