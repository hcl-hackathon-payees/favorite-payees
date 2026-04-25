package com.hcl.favouritePayee.service;

import com.hcl.favouritePayee.dto.FavoritePayeeResponse;
import com.hcl.favouritePayee.repository.FavoritePayeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PayeesService {

    private final FavoritePayeeRepository repository;

    public Page<FavoritePayeeResponse> getFavoriteAccounts(Long customerId, Pageable pageable) {
        return repository.findByCustomerId(customerId,pageable);
    }

}