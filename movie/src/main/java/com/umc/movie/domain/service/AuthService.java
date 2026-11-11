package com.umc.movie.domain.service;

import com.umc.movie.domain.dto.request.LoginRequest;
import com.umc.movie.domain.dto.response.LoginResponse;
import com.umc.movie.domain.entity.Member;
import com.umc.movie.domain.repository.MemberRepository;
import com.umc.movie.global.error.ApiException;
import com.umc.movie.global.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {

        // 이메일로 회원 조회
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(() -> new ApiException(
                        HttpStatus.UNAUTHORIZED,
                        "AUTH401_1",
                        "이메일 또는 비밀번호가 올바르지 않습니다."
                ));

        // 입력 비밀번호와 DB에 저장된 해시 비교
        if (!passwordEncoder.matches(
                request.password(),
                member.getPassword()
        )) {
            throw new ApiException(
                    HttpStatus.UNAUTHORIZED,
                    "AUTH401_1",
                    "이메일 또는 비밀번호가 올바르지 않습니다."
            );
        }

        // JWT 발급
        String accessToken = jwtUtil.createAccessToken(
                member.getMemberId(),
                member.getEmail()
        );

        return new LoginResponse(
                member.getNickname(),
                accessToken
        );
    }
}
