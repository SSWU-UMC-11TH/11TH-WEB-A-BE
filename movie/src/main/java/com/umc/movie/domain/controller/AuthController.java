package com.umc.movie.domain.controller;

import com.umc.movie.domain.dto.request.LoginRequest;
import com.umc.movie.domain.dto.response.LoginResponse;
import com.umc.movie.domain.service.AuthService;
import com.umc.movie.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request
    ) {
        LoginResponse response = authService.login(request);

        return ResponseEntity.ok(
                ApiResponse.ok("로그인에 성공했습니다.", response)
        );
    }
}
