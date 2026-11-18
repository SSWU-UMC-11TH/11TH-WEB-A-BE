package com.umc.movie.domain.controller;

import com.umc.movie.domain.dto.request.CreateMovieRecordRequest;
import com.umc.movie.domain.dto.response.MovieRecordResponse;
import com.umc.movie.domain.service.MovieRecordService;
import com.umc.movie.global.common.ApiResponse;
import com.umc.movie.global.interceptor.AuthInterceptor;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/movie-records")
public class MovieRecordController {

    private final MovieRecordService movieRecordService;

    @PostMapping
    public ResponseEntity<ApiResponse<MovieRecordResponse>> create(
            @RequestAttribute(AuthInterceptor.MEMBER_ID) Long memberId,
            @RequestBody @Valid CreateMovieRecordRequest request
    ) {
        MovieRecordResponse response =
                movieRecordService.create(memberId, request);

        return ResponseEntity.status(201)
                .body(ApiResponse.created(
                        "영화 기록을 저장했습니다.",
                        response
                ));
    }
}
