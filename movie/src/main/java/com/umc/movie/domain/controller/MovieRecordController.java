package com.umc.movie.domain.controller;

import com.umc.movie.domain.dto.request.CreateMovieRecordRequest;
import com.umc.movie.domain.dto.response.MovieRecordResponse;
import com.umc.movie.domain.service.MovieRecordService;
import com.umc.movie.global.common.ApiResponse;
import com.umc.movie.global.interceptor.AuthInterceptor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequiredArgsConstructor
@RequestMapping("/movie-records")
@Tag(name = "영화 기록", description = "영화 기록 API")
@SecurityRequirement(name = "bearerAuth")
public class MovieRecordController {

    private final MovieRecordService movieRecordService;

    @Operation(
            summary = "영화 기록 생성",
            description = """
                    주어진 영화 기록을 생성합니다.
                    """
    )
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
