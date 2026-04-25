package com.hcl.favouritePayee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteAccountResponse {

    private Long id;
    private Long customerId;
    private String accountName;
    private String iban;
    private String bankName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}