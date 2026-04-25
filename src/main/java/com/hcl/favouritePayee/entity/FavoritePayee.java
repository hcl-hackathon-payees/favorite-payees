package com.hcl.favouritePayee.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @NotNull(message = "account name is required")
    @Size(min=3,max = 15)
    @Column(name = "account_name", nullable = false)
    private String accountName;

    @NotNull(message = "iban is required")
    @Size(max=20)
    @Column(name = "iban", nullable = false)
    private String iban;

    @Column(name = "bank_name")
    private String bankName;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
