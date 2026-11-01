package com.umc.week03.domain.controller;

import com.umc.week03.domain.dto.CreateRentalRequest;
import com.umc.week03.domain.service.RentalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/rentals")
@RequiredArgsConstructor
public class RentalController {

    private final RentalService rentalService;

    @PostMapping
    public String createRental(@Valid @RequestBody CreateRentalRequest request) {
        rentalService.createRental(request);
        return "도서 대여가 완료되었습니다!";
    }

    @PatchMapping("/{rentalId}/return")
    public String returnRental(@PathVariable Long rentalId) {
        if (!rentalService.returnRental(rentalId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "존재하지 않거나 이미 반납된 대여입니다."
            );
        }

        return "도서 반납이 완료되었습니다!";
    }
}
