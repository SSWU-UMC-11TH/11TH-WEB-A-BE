package com.umc.movie.domain.controller;

import com.umc.movie.domain.dto.request.MemberUpdateRequest;
import com.umc.movie.domain.dto.request.SignUpRequest;
import com.umc.movie.domain.dto.response.MemberInfoResponse;
import com.umc.movie.global.common.ApiResponse;
import com.umc.movie.domain.dto.response.EmailCheckResponse;
import com.umc.movie.domain.dto.response.MemberResponse;
import com.umc.movie.domain.dto.response.NicknameCheckResponse;
import com.umc.movie.domain.service.MemberService;
import com.umc.movie.global.interceptor.AuthInterceptor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
@Tag(name = "회원", description = "회원 관련 API")
public class MemberController {

    private final MemberService memberService;

    @Operation(
            summary = "닉네임 중복 확인",
            description = """
                    주어진 닉네임이 이미 사용 중인지 확인합니다.
                    """
    )
    @GetMapping("/nickname/{nickname}")
    public NicknameCheckResponse checkNickname(@PathVariable String nickname) {
        return memberService.checkNickname(nickname);
    }

    @Operation(
            summary = "이메일 중복 확인",
            description = """
                    주어진 이메일이 이미 사용 중인지 확인합니다.
                    """
    )
    @GetMapping("/email/{email}")
    public EmailCheckResponse checkEmail(@PathVariable String email) {
        return memberService.checkEmail(email);
    }

    @Operation(
            summary = "회원가입",
            description = """
                    주어진 정보로 회원가입을 진행합니다.
                    """
    )
    @PostMapping
    public ResponseEntity<ApiResponse<MemberResponse>> signUp(
            @Valid @RequestBody SignUpRequest request
    ) {
        MemberResponse response = memberService.signUp(request);

        return ResponseEntity.status(201)
                .body(ApiResponse.created("회원가입에 성공했습니다.", response));
    }

    @Operation(
            summary = "회원 정보 조회",
            description = """
                    회원의 정보를 조회합니다.
                    """
    )
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MemberInfoResponse>> getMyInfo(
            @RequestAttribute(AuthInterceptor.MEMBER_ID) Long memberId
    ) {
        MemberInfoResponse response = memberService.getMyInfo(memberId);

        return ResponseEntity.ok(
                ApiResponse.ok("회원 정보를 조회했습니다.", response)
        );
    }

    @Operation(
            summary = "회원 정보 수정",
            description = """
                    회원의 정보를 수정합니다.
                    """
    )
    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<MemberInfoResponse>> updateMyInfo(
            @RequestAttribute(AuthInterceptor.MEMBER_ID) Long memberId,
            @Valid @RequestBody MemberUpdateRequest request
    ) {
        MemberInfoResponse response =
                memberService.updateMyInfo(memberId, request);

        return ResponseEntity.ok(
                ApiResponse.ok("회원 정보를 수정했습니다.", response)
        );
    }
}