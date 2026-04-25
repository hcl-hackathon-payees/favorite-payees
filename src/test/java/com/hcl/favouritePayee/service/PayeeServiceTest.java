package com.hcl.favouritePayee.service;

import com.hcl.favouritePayee.dto.CreateFavoriteAccountRequest;
import com.hcl.favouritePayee.dto.FavoritePayeeResponse;
import com.hcl.favouritePayee.entity.BankCodeMapping;
import com.hcl.favouritePayee.entity.Customer;
import com.hcl.favouritePayee.entity.FavoritePayee;
import com.hcl.favouritePayee.exception.ResourceNotFoundException;
import com.hcl.favouritePayee.repository.BankCodeRepository;
import com.hcl.favouritePayee.repository.CustomerRepository;
import com.hcl.favouritePayee.repository.FavoritePayeeRepository;
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
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PayeesServiceTest {

    @Mock
    private FavoritePayeeRepository repository;

    @Mock
    private BankCodeRepository bankCodeRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private PayeesService payeesService;

    private Customer sampleCustomer;
    private FavoritePayee samplePayee;

    @BeforeEach
    void setUp() {
        sampleCustomer = new Customer();
        sampleCustomer.setId(123L);

        samplePayee = FavoritePayee.builder()
                .id(1L)
                .customer(sampleCustomer)
                .accountName("John Doe")
                .iban("NL00RABO0123456789")
                .bankName("Rabobank")
                .build();
    }

    @Test
    @DisplayName("Should return paged favorite accounts for a customer")
    void getFavoriteAccounts_Success() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<FavoritePayee> page = new PageImpl<>(Collections.singletonList(samplePayee));

        when(repository.findByCustomerId(123L, pageable)).thenReturn(page);

        Page<FavoritePayee> result = payeesService.getFavoriteAccounts(123L, pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(repository).findByCustomerId(123L, pageable);
    }

    @Test
    @DisplayName("Should resolve bank and create account successfully")
    void createFavoriteAccount_Success() {
        
        CreateFavoriteAccountRequest request = new CreateFavoriteAccountRequest();
        request.setAccountName("John Doe");
        request.setIban("NL00RABO0123456789");

        BankCodeMapping mapping = new BankCodeMapping();
        mapping.setBankName("Rabobank");

        when(customerRepository.findById(123L)).thenReturn(Optional.of(sampleCustomer));
        when(bankCodeRepository.findById("RABO")).thenReturn(Optional.of(mapping));
        when(repository.save(any(FavoritePayee.class))).thenReturn(samplePayee);

        FavoritePayeeResponse response = payeesService.createFavoriteAccount(123L, request);

        
        assertThat(response).isNotNull();
        assertThat(response.getBankName()).isEqualTo("Rabobank");
        verify(repository).save(any(FavoritePayee.class));
    }

    @Test
    @DisplayName("Should set 'Unknown Bank' if bank code is not found")
    void createFavoriteAccount_UnknownBank() {
        CreateFavoriteAccountRequest request = new CreateFavoriteAccountRequest();
        request.setIban("NL00XXXX0123456789");

        when(customerRepository.findById(123L)).thenReturn(Optional.of(sampleCustomer));
        when(bankCodeRepository.findById("XXXX")).thenReturn(Optional.empty());
        when(repository.save(any(FavoritePayee.class))).thenAnswer(i -> i.getArguments()[0]);

        FavoritePayeeResponse response = payeesService.createFavoriteAccount(123L, request);

        assertThat(response.getBankName()).isEqualTo("Unknown Bank");
    }

    @Test
    @DisplayName("Should throw Exception when customer is not found")
    void createFavoriteAccount_CustomerNotFound() {
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> payeesService.createFavoriteAccount(999L, new CreateFavoriteAccountRequest()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Customer not found");
    }

    @Test
    @DisplayName("Should throw ResponseStatusException when account ID is invalid")
    void getFavoriteAccount_NotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> payeesService.getFavoriteAccount(1L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("404");
    }

    @Test
    @DisplayName("Should delete account when customer and account IDs match")
    void deleteFavoriteAccount_Success() {
        when(repository.findByIdAndCustomerId(1L, 123L)).thenReturn(Optional.of(samplePayee));

        payeesService.deleteFavoriteAccount(123L, 1L);

        verify(repository).delete(samplePayee);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException during deletion if mismatch occurs")
    void deleteFavoriteAccount_NotFound() {
        when(repository.findByIdAndCustomerId(1L, 123L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> payeesService.deleteFavoriteAccount(123L, 1L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(repository, never()).delete(any());
    }
}