package com.hcl.favouritePayee.service;

import com.hcl.favouritePayee.dto.BankResolutionResponse;
import com.hcl.favouritePayee.entity.BankCodeMapping;
import com.hcl.favouritePayee.exception.BankNotFoundException;
import com.hcl.favouritePayee.repository.BankCodeRepository;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IbanValidationServiceTest {

    @Mock
    private BankCodeRepository bankCodeRepository;

    @InjectMocks
    private PayeesService payeesService;

    private BankCodeMapping bankCodeMapping;

    @BeforeEach
    void setUp() {
        bankCodeMapping = BankCodeMapping.builder()
                .code("2134")
                .bankName("Santander")
                .build();
    }

    @Test
    @DisplayName("Should return bank resolution for valid IBAN")
    void validateIban_ValidIban_ShouldReturnBankResolution() {
        String iban = "ES21213400000000000";
        when(bankCodeRepository.findById("2134")).thenReturn(Optional.of(bankCodeMapping));

        BankResolutionResponse response = payeesService.validateIban(iban);

        assertThat(response.getBankCode()).isEqualTo("2134");
        assertThat(response.getBankName()).isEqualTo("Santander");
    }

    @Test
    @DisplayName("Should extract bank code from positions 5-8")
    void validateIban_ShouldExtractBankCodeFromPositions5To8() {
        String iban = "ES21999900000000000";
        BankCodeMapping customMapping = BankCodeMapping.builder()
                .code("9999")
                .bankName("Custom Bank")
                .build();
        when(bankCodeRepository.findById("9999")).thenReturn(Optional.of(customMapping));

        BankResolutionResponse response = payeesService.validateIban(iban);

        assertThat(response.getBankCode()).isEqualTo("9999");
        assertThat(response.getBankName()).isEqualTo("Custom Bank");
    }

    @Test
    @DisplayName("Should throw exception when IBAN exceeds 20 characters")
    void validateIban_IbanExceeds20Chars_ShouldThrowException() {
        String iban = "ES21213400000000000000000000";

        assertThatThrownBy(() -> payeesService.validateIban(iban))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("IBAN must not be greater than 20 characters");
    }

    @Test
    @DisplayName("Should throw exception when IBAN is null")
    void validateIban_IbanIsNull_ShouldThrowException() {
        assertThatThrownBy(() -> payeesService.validateIban(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("IBAN must not be greater than 20 characters");
    }

    @Test
    @DisplayName("Should throw BankNotFoundException when bank code not found")
    void validateIban_BankCodeNotFound_ShouldThrowException() {
        String iban = "ES21999900000000000";
        when(bankCodeRepository.findById("9999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> payeesService.validateIban(iban))
                .isInstanceOf(BankNotFoundException.class)
                .hasMessage("Bank not found for code: 9999");
    }

    @Test
    @DisplayName("Should validate IBAN with exactly 20 characters")
    void validateIban_Exactly20Chars_ShouldReturnBankResolution() {
        String iban = "ES2121340000000000"; // exactly 20 chars
        when(bankCodeRepository.findById("2134")).thenReturn(Optional.of(bankCodeMapping));

        BankResolutionResponse response = payeesService.validateIban(iban);

        assertThat(response.getBankCode()).isEqualTo("2134");
        assertThat(response.getBankName()).isEqualTo("Santander");
    }

    @Test
    @DisplayName("Should throw exception when IBAN is less than 8 characters")
    void validateIban_LessThan8Chars_ShouldThrowException() {
        String iban = "ES21213";

        assertThatThrownBy(() -> payeesService.validateIban(iban))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("IBAN must be at least 8 characters");
    }
}