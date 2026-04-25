package com.hcl.favouritePayee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFavoriteAccountRequest {

    private String accountName;
    private String iban;
}