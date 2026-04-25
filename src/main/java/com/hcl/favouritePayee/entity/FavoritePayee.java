package com.hcl.favouritePayee.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "favorite_accounts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoritePayee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Customer ID
    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    // Account Name
    @Column(name = "account_name", nullable = false)
    @NotBlank(message = "Account name is required")
    @Pattern(
            regexp = "^[a-zA-Z0-9'\\- ]+$",
            message = "Account name can contain letters, numbers, spaces, ' and -"
    )
    private String accountName;

    // IBAN
    @Column(name = "iban", nullable = false, length = 20)
    @NotBlank(message = "IBAN is required")
    @Pattern(
            regexp = "^[a-zA-Z0-9]+$",
            message = "IBAN must contain only letters and numbers"
    )
    @Size(max = 20, message = "IBAN max length is 20")
    private String iban;

    // Bank (auto-filled)
    @Column(name = "bank_name", nullable = false)
    private String bankName;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
