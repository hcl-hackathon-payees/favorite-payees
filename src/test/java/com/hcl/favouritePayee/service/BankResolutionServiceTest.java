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
class BankResolutionServiceTest {

    @Mock
    private BankCodeRepository bankCodeRepository;

    @InjectMocks
    private PayeesService payeesService;

    private BankCodeMapping bankCodeMapping;

    @BeforeEach
    void setUp() {
        bankCodeMapping = BankCodeMapping.builder()
                .code("0000")
                .bankName("Santander")
                .build();
    }

//    @Test
//    @DisplayName("Should extract bank code from IBAN with spaces")
//    void extractBankCodeFromIban_WithSpaces_ShouldExtractCode() {
//        String iban = "ES21 0000 0000 00 00000";
//        String bankCode = payeesService.extractBankCodeFromIban(iban);
//        assertThat(bankCode).isEqualTo("0000");
//    }

//    @Test
//    @DisplayName("Should extract bank code from IBAN without spaces")
//    void extractBankCodeFromIban_WithoutSpaces_ShouldExtractCode() {
//        String iban = "ES21000000000000000";
//        String bankCode = payeesService.extractBankCodeFromIban(iban);
//        assertThat(bankCode).isEqualTo("0000");
//    }

//    @Test
//    @DisplayName("Should throw exception for invalid IBAN length")
//    void extractBankCodeFromIban_InvalidLength_ShouldThrowException() {
//        String iban = "ES2100";
//        assertThatThrownBy(() -> payeesService.extractBankCodeFromIban(iban))
//                .isInstanceOf(IllegalArgumentException.class)
//                .hasMessage("Invalid IBAN length");
//    }

//    @Test
//    @DisplayName("Should return bank name for valid bank code")
//    void getBankFromIban_ValidCode_ShouldReturnBankName() {
//        when(bankCodeRepository.findById("0000")).thenReturn(Optional.of(bankCodeMapping));
//
//        BankResolutionResponse response = payeesService.getBankFromIban("ES21000000000000000");
//
//        assertThat(response.getBankCode()).isEqualTo("0000");
//        assertThat(response.getBankName()).isEqualTo("Santander");
//    }
//
//    @Test
//    @DisplayName("Should throw BankNotFoundException for unknown bank code")
//    void getBankFromIban_UnknownCode_ShouldThrowException() {
//        when(bankCodeRepository.findById("9999")).thenReturn(Optional.empty());
//
//        assertThatThrownBy(() -> payeesService.getBankFromIban("ES21999900000000000"))
//                .isInstanceOf(BankNotFoundException.class)
//                .hasMessage("Bank not found for code: 9999");
//    }
//
//    @Test
//    @DisplayName("Should correctly extract different bank codes")
//    void extractBankCodeFromIban_DifferentCodes_ShouldExtractCorrectly() {
//        assertThat(payeesService.extractBankCodeFromIban("ES21444400000000000")).isEqualTo("4444");
//        assertThat(payeesService.extractBankCodeFromIban("ES21555500000000000")).isEqualTo("5555");
//        assertThat(payeesService.extractBankCodeFromIban("DE89123400000000000")).isEqualTo("1234");
//    }
}