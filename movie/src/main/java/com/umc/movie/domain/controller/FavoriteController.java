package com.umc.movie.domain.controller;

import com.umc.movie.domain.dto.request.CreateFavoriteRequest;
import com.umc.movie.domain.dto.response.FavoriteResponse;
import com.umc.movie.domain.dto.response.FavoriteStatusResponse;
import com.umc.movie.domain.service.FavoriteService;
import com.umc.movie.global.common.ApiResponse;
import com.umc.movie.global.interceptor.AuthInterceptor;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    // 즐겨찾기 등록
    @PostMapping
    public ResponseEntity<ApiResponse<FavoriteResponse>> create(
            @RequestAttribute(AuthInterceptor.MEMBER_ID) Long memberId,
            @RequestBody @Valid CreateFavoriteRequest request
    ) {
        FavoriteResponse response =
                favoriteService.create(memberId, request);

        return ResponseEntity.status(201)
                .body(ApiResponse.created(
                        "즐겨찾기를 등록했습니다.",
                        response
                ));
    }

    // 내 즐겨찾기 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<FavoriteResponse>>> getMyFavorites(
            @RequestAttribute(AuthInterceptor.MEMBER_ID) Long memberId
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(
                        "내 즐겨찾기 목록을 조회했습니다.",
                        favoriteService.getMyFavorites(memberId)
                )
        );
    }

    // 즐겨찾기 여부 조회
    @GetMapping("/{movieId}")
    public ResponseEntity<ApiResponse<FavoriteStatusResponse>> getStatus(
            @RequestAttribute(AuthInterceptor.MEMBER_ID) Long memberId,
            @PathVariable Long movieId
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(
                        "즐겨찾기 여부를 조회했습니다.",
                        favoriteService.getStatus(memberId, movieId)
                )
        );
    }

    // 즐겨찾기 해제
    @DeleteMapping("/{movieId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @RequestAttribute(AuthInterceptor.MEMBER_ID) Long memberId,
            @PathVariable Long movieId
    ) {
        favoriteService.delete(memberId, movieId);

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "즐겨찾기를 해제했습니다.",
                        null
                )
        );
    }
}
