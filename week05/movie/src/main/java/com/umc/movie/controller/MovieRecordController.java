
package com.umc.movie.controller;

import com.umc.movie.config.AuthInterceptor;
import com.umc.movie.dto.ApiResponse;
import com.umc.movie.dto.CreateMovieRecordRequest;
import com.umc.movie.dto.MovieRecordResponse;
import com.umc.movie.service.MovieRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/movie-records")
public class MovieRecordController {

    private final MovieRecordService movieRecordService;

    @PostMapping
    public ResponseEntity<ApiResponse<MovieRecordResponse>> createMovieRecord(
            @RequestAttribute(AuthInterceptor.MEMBER_ID) Long memberId,
            @Valid @RequestBody CreateMovieRecordRequest request
    ) {
        MovieRecordResponse response =
                movieRecordService.createMovieRecord(memberId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "COMMON201",
                        "영화 기록이 등록되었습니다.",
                        response
                ));
    }
}
