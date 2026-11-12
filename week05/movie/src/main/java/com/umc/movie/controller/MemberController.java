package com.umc.movie.controller;

import com.umc.movie.config.AuthInterceptor;
import com.umc.movie.dto.*;
import com.umc.movie.service.MemberService;
import com.umc.movie.service.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final RatingService ratingService;

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

    @GetMapping("/nickname/{nickname}")
    public AvailabilityResponse checkNickname(
            @PathVariable String nickname
    ) {
        return memberService.checkNickname(nickname);
    }

    @GetMapping("/email/{email}")
    public AvailabilityResponse checkEmail(
            @PathVariable String email
    ) {
        return memberService.checkEmail(email);
    }

    @GetMapping("/{memberId}/ratings")
    public List<RatingResponse> getRatings(
            @PathVariable Long memberId
    ) {
        return ratingService.getRatingsByMember(memberId);
    }

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