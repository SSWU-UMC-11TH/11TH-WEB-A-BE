
package com.umc.movie.controller;

import com.umc.movie.dto.CreateRatingRequest;
import com.umc.movie.dto.RatingResponse;
import com.umc.movie.dto.UpdateRatingRequest;
import com.umc.movie.service.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members/{memberId}/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @PostMapping
    public ResponseEntity<RatingResponse> createRating(
            @PathVariable Long memberId,
            @Valid @RequestBody CreateRatingRequest request
    ) {
        RatingResponse response = ratingService.createRating(memberId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PatchMapping("/{ratingId}")
    public ResponseEntity<RatingResponse> updateRating(
            @PathVariable Long memberId,
            @PathVariable Long ratingId,
            @Valid @RequestBody UpdateRatingRequest request
    ) {
        RatingResponse response = ratingService.updateRating(
                memberId, ratingId, request
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{ratingId}")
    public ResponseEntity<Void> deleteRating(
            @PathVariable Long memberId,
            @PathVariable Long ratingId
    ) {
        ratingService.deleteRating(memberId, ratingId);
        return ResponseEntity.noContent().build();
    }
}
