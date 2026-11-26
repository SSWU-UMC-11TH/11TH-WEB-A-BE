package com.umc.movie.controller;

import com.umc.movie.config.AuthInterceptor;
import com.umc.movie.config.SwaggerConfig;
import com.umc.movie.dto.*;
import com.umc.movie.service.MemberService;
import com.umc.movie.service.RatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "회원", description = "회원가입과 회원 정보 API")
@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final RatingService ratingService;

    @Operation(summary = "회원가입", description = "토큰이 필요하지 않습니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201", description = "MEMBER_CREATED: 회원가입 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = "INVALID_INPUT: 입력값 오류"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409", description = "DUPLICATE_RESOURCE: 이메일 또는 닉네임 중복")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<MemberResponse>> signUp(
            @Valid @RequestBody SignUpRequest request
    ) {
        MemberResponse response = memberService.signUp(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "MEMBER_CREATED",
                        "회원가입이 완료되었습니다.",
                        response
                ));
    }

    @Operation(summary = "닉네임 중복 확인")
    @GetMapping("/nickname/{nickname}")
    public AvailabilityResponse checkNickname(
            @PathVariable String nickname
    ) {
        return memberService.checkNickname(nickname);
    }

    @Operation(summary = "이메일 중복 확인")
    @GetMapping("/email/{email}")
    public AvailabilityResponse checkEmail(
            @PathVariable String email
    ) {
        return memberService.checkEmail(email);
    }

    @Operation(
            summary = "회원별 평점 목록 조회",
            description = "특정 회원의 평점 목록을 조회합니다. 토큰이 필요하지 않습니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "MEMBER404_1: 회원을 찾을 수 없음")
    })
    @GetMapping("/{memberId}/ratings")
    public List<RatingResponse> getRatings(
            @PathVariable Long memberId
    ) {
        return ratingService.getRatingsByMember(memberId);
    }

    @Operation(
            summary = "내 정보 조회",
            description = "JWT 토큰이 필요합니다.",
            security = @SecurityRequirement(name = SwaggerConfig.BEARER_AUTH)
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "COMMON200: 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", description = "AUTH401_2 / AUTH401_3 / AUTH401_4: 인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "MEMBER404_1: 회원을 찾을 수 없음")
    })
    @GetMapping("/me")
    public ApiResponse<MemberInfoResponse> getMyInfo(
            @RequestAttribute(AuthInterceptor.MEMBER_ID) Long memberId
    ) {
        return ApiResponse.success(
                "COMMON200",
                "내 정보 조회에 성공했습니다.",
                memberService.getMyInfo(memberId)
        );
    }

    @Operation(
            summary = "내 정보 수정",
            description = "JWT 토큰이 필요합니다.",
            security = @SecurityRequirement(name = SwaggerConfig.BEARER_AUTH)
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "COMMON200: 수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = "INVALID_INPUT: 입력값 오류"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", description = "AUTH401_2 / AUTH401_3 / AUTH401_4: 인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "MEMBER404_1: 회원을 찾을 수 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409", description = "MEMBER409_1: 닉네임 중복")
    })
    @PatchMapping("/me")
    public ApiResponse<MemberInfoResponse> updateMyInfo(
            @RequestAttribute(AuthInterceptor.MEMBER_ID) Long memberId,
            @Valid @RequestBody MemberUpdateRequest request
    ) {
        return ApiResponse.success(
                "COMMON200",
                "내 정보 수정에 성공했습니다.",
                memberService.updateMyInfo(memberId, request)
        );
    }
}