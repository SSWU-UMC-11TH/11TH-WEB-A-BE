package com.umc.week03.domain.service;

import com.umc.week03.domain.dto.CreateRentalRequest;
import com.umc.week03.domain.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RentalService {

    private final RentalRepository rentalRepository;

    public void createRental(CreateRentalRequest request) {
        rentalRepository.save(request);
    }

    public boolean returnRental(Long rentalId) {
        return rentalRepository.returnRental(rentalId) > 0;
    }
}