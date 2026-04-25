package com.hcl.favouritePayee.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateFavoriteAccountRequest {

    @NotBlank(message = "Account name is required")
    private String accountName;

    @NotBlank(message = "IBAN is required")
    private String iban;
}