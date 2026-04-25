package com.hcl.favouritePayee.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bank_code_mapping")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankCodeMapping {
    @Id
    @Column(name = "code", nullable = false, length = 10)
    private String code;

    @Column(name = "bank_name", nullable = false, length = 100)
    private String bankName;

}
