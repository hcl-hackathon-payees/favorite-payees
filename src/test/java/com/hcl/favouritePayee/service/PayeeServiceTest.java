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
        when(repository.save(any(FavoritePayee.class))).thenAnswer(i -> i.getArguments()[0]);

        FavoritePayeeResponse response = payeesService.createFavoriteAccount(123L, request);

        assertThat(response).isNotNull();
        assertThat(response.getBankName()).isEqualTo("Rabobank");
        verify(repository).save(any(FavoritePayee.class));
    }

    @Test
    @DisplayName("Should set 'Unknown Bank' if bank code is not found during creation")
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

    // --- NEW: Update Payee Tests ---

    @Test
    @DisplayName("Should fully update payee details and re-resolve bank")
    void updateFavoriteAccount_FullUpdate() {
        UpdateFavoriteAccountRequest request = new UpdateFavoriteAccountRequest();
        request.setAccountName("Jane Doe");
        request.setIban("NL00INGB0123456789");

        BankCodeMapping ingMapping = new BankCodeMapping();
        ingMapping.setBankName("ING Bank");

        when(repository.findById(1L)).thenReturn(Optional.of(samplePayee));
        when(bankCodeRepository.findById("INGB")).thenReturn(Optional.of(ingMapping));
        when(repository.save(any(FavoritePayee.class))).thenAnswer(i -> i.getArguments()[0]);

        FavoritePayeeResponse response = payeesService.updateFavoriteAccount(1L, request);

        assertThat(response.getAccountName()).isEqualTo("Jane Doe");
        assertThat(response.getIban()).isEqualTo("NL00INGB0123456789");
        assertThat(response.getBankName()).isEqualTo("ING Bank"); // Ensures re-resolution worked
    }

    @Test
    @DisplayName("Should partially update payee (only name, IBAN remains unchanged)")
    void updateFavoriteAccount_PartialUpdate() {
        UpdateFavoriteAccountRequest request = new UpdateFavoriteAccountRequest();
        request.setAccountName("Jane Doe");
        // IBAN is explicitly left null

        when(repository.findById(1L)).thenReturn(Optional.of(samplePayee));
        when(repository.save(any(FavoritePayee.class))).thenAnswer(i -> i.getArguments()[0]);

        FavoritePayeeResponse response = payeesService.updateFavoriteAccount(1L, request);

        assertThat(response.getAccountName()).isEqualTo("Jane Doe");
        assertThat(response.getIban()).isEqualTo("NL00RABO0123456789"); // Should keep original
        assertThat(response.getBankName()).isEqualTo("Rabobank"); // Should keep original

        verify(bankCodeRepository, never()).findById(anyString());
    }

    @Test
    @DisplayName("Should throw ResponseStatusException when updating non-existent payee")
    void updateFavoriteAccount_NotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> payeesService.updateFavoriteAccount(1L, new UpdateFavoriteAccountRequest()))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("404");
    }

    // --- UPDATED: Delete Payee Tests ---

    @Test
    @DisplayName("Should delete account successfully")
    void deleteFavoriteAccount_Success() {
        when(repository.findById(1L)).thenReturn(Optional.of(samplePayee));

        payeesService.deleteFavoriteAccount(1L);

        verify(repository).delete(samplePayee);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException during deletion if payee not found")
    void deleteFavoriteAccount_NotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> payeesService.deleteFavoriteAccount(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Favorite account not found with id");

        verify(repository, never()).delete(any());
    }
}