package com.hcl.favouritePayee.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "customers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;
}