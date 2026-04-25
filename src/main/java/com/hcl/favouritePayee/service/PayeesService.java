package com.hcl.favouritePayee.service;


import com.hcl.favouritePayee.dto.FavoritePayeeResponse;
import com.hcl.favouritePayee.repository.FavoritePayeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PayeesService {

    @Autowired
    FavoritePayeeRepository payeeRepository;

    public Page<FavoritePayeeResponse> getFavoriteAccounts(Long customerId, Pageable pageable) {
        return payeeRepository.findByCustomerId(customerId,pageable);
    }
}