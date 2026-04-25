package com.hcl.favouritePayee.repository;

import com.hcl.favouritePayee.entity.BankCodeMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankCodeRepository extends JpaRepository<BankCodeMapping, String> {
}