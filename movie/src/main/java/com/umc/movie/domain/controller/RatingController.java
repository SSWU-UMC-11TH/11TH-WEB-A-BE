package com.umc.movie.domain.controller;

import com.umc.movie.domain.dto.request.CreateRatingRequest;
import com.umc.movie.domain.dto.request.UpdateRatingRequest;
import com.umc.movie.domain.dto.response.RatingResponse;
import com.umc.movie.domain.service.RatingService;
import com.umc.movie.global.common.ApiResponse;
import com.umc.movie.global.interceptor.AuthInterceptor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequiredArgsConstructor
@Tag(name = "평점", description = "회원 평점 API")
@SecurityRequirement(name = "bearerAuth")
public class RatingController {

    private final RatingService ratingService;

    @Operation(
            summary = "회원 평점 조회",
            description = """
                    주어진 회원의 평점 목록을 조회합니다.
                    """
    )
    @GetMapping("/members/{memberId}/ratings")
    public Page<RatingResponse> getMemberRatings(
            @PathVariable Long memberId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ratingService.getRatingsByMember(memberId, page, size);
    }

    @Operation(
            summary = "평점 생성",
            description = """
                    주어진 평점을 생성합니다.
                    """
    )
    @PostMapping("/ratings")
    public ResponseEntity<ApiResponse<Long>> create(
            @RequestAttribute(AuthInterceptor.MEMBER_ID) Long memberId,
            @Valid @RequestBody CreateRatingRequest request
    ) {
        Long ratingId = ratingService.create(memberId, request);

        return ResponseEntity.status(201)
                .body(ApiResponse.created(
                        "평점 등록에 성공했습니다.",
                        ratingId
                ));
    }

    @Operation(
            summary = "평점 수정",
            description = """
                    주어진 평점을 수정합니다.
                    """
    )
    @PatchMapping("/ratings/{ratingId}")
    public ResponseEntity<ApiResponse<Void>> update(
            @RequestAttribute(AuthInterceptor.MEMBER_ID) Long memberId,
            @PathVariable Long ratingId,
            @Valid @RequestBody UpdateRatingRequest request
    ) {
        ratingService.update(ratingId, memberId, request);

        return ResponseEntity.ok(
                ApiResponse.ok("평점 수정에 성공했습니다.", null)
        );
    }

    @Operation(
            summary = "평점 삭제",
            description = """
                    주어진 평점을 삭제합니다.
                    """
    )
    @DeleteMapping("/ratings/{ratingId}")
    public ResponseEntity<Void> delete(
            @RequestAttribute(AuthInterceptor.MEMBER_ID) Long memberId,
            @PathVariable Long ratingId
    ) {
        ratingService.delete(ratingId, memberId);

        return ResponseEntity.noContent().build();
    }
}
