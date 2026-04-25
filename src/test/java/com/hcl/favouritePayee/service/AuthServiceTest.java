package com.hcl.favouritePayee.service;

import com.hcl.favouritePayee.dto.LoginResponse;
import com.hcl.favouritePayee.dto.RegisterRequest;
import com.hcl.favouritePayee.entity.Customer;
import com.hcl.favouritePayee.exception.CustomerNotFoundException;
import com.hcl.favouritePayee.exception.DuplicateCustomerException;
import com.hcl.favouritePayee.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private AuthService authService;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = Customer.builder()
                .id(1L)
                .name("John Doe")
                .build();
    }

    @Test
    @DisplayName("Should return login response for valid customer ID")
    void validateCustomer_ValidId_ShouldReturnLoginResponse() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        LoginResponse response = authService.validateCustomer(1L);

        assertThat(response.getCustomerId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("John Doe");
        assertThat(response.getMessage()).isEqualTo("Customer validated successfully");
    }

    @Test
    @DisplayName("Should throw CustomerNotFoundException for invalid customer ID")
    void validateCustomer_InvalidId_ShouldThrowException() {
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.validateCustomer(999L))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessage("Customer not found with id: 999")
                .satisfies(ex -> assertThat(((CustomerNotFoundException) ex).getCustomerId()).isEqualTo(999L));
    }

    @Test
    @DisplayName("Should return correct customer ID in response")
    void validateCustomer_ShouldReturnCorrectCustomerId() {
        Customer differentCustomer = Customer.builder()
                .id(42L)
                .name("Jane Smith")
                .build();
        when(customerRepository.findById(42L)).thenReturn(Optional.of(differentCustomer));

        LoginResponse response = authService.validateCustomer(42L);

        assertThat(response.getCustomerId()).isEqualTo(42L);
        assertThat(response.getName()).isEqualTo("Jane Smith");
    }

    @Test
    @DisplayName("Should register new customer successfully")
    void registerCustomer_NewCustomer_ShouldReturnLoginResponse() {
        RegisterRequest request = RegisterRequest.builder()
                .customerId(100L)
                .name("New Customer")
                .build();
        when(customerRepository.existsById(100L)).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LoginResponse response = authService.registerCustomer(request);

        assertThat(response.getCustomerId()).isEqualTo(100L);
        assertThat(response.getName()).isEqualTo("New Customer");
        assertThat(response.getMessage()).isEqualTo("Customer registered successfully");
    }

    @Test
    @DisplayName("Should throw DuplicateCustomerException when customer ID already exists")
    void registerCustomer_DuplicateId_ShouldThrowException() {
        RegisterRequest request = RegisterRequest.builder()
                .customerId(1L)
                .name("Duplicate Customer")
                .build();
        when(customerRepository.existsById(1L)).thenReturn(true);

        assertThatThrownBy(() -> authService.registerCustomer(request))
                .isInstanceOf(DuplicateCustomerException.class)
                .hasMessage("Customer with id 1 already exists")
                .satisfies(ex -> assertThat(((DuplicateCustomerException) ex).getCustomerId()).isEqualTo(1L));
    }
}