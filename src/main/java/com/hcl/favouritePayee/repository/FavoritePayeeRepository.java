package com.hcl.favouritePayee.repository;

import com.hcl.favouritePayee.dto.FavoritePayeeResponse;
import com.hcl.favouritePayee.entity.FavoritePayee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FavoritePayeeRepository extends JpaRepository<FavoritePayee, Long> {

    Page<FavoritePayee> findByCustomerIdOrderByCreatedAtDesc(Long customerId, Pageable pageable);

    Optional<FavoritePayee> findByIdAndCustomerId(Long id, Long customerId);

    Page<FavoritePayeeResponse> findByCustomerId(Long customerId, Pageable pageable);
}