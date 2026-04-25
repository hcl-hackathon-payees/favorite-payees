package com.hcl.favouritePayee.repository;

import com.hcl.favouritePayee.entity.FavoriteAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FavoritePayeeRepository extends JpaRepository<FavoriteAccount, Long> {

    Page<FavoriteAccount> findByCustomerIdOrderByCreatedAtDesc(Long customerId, Pageable pageable);

    Optional<FavoriteAccount> findByIdAndCustomerId(Long id, Long customerId);
}