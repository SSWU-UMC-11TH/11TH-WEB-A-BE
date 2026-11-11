package com.umc.movie.domain.service;

import com.umc.movie.domain.dto.request.SignUpRequest;
import com.umc.movie.domain.dto.response.EmailCheckResponse;
import com.umc.movie.domain.dto.response.MemberInfoResponse;
import com.umc.movie.domain.dto.response.MemberResponse;
import com.umc.movie.domain.dto.response.NicknameCheckResponse;
import com.umc.movie.domain.entity.Member;
import com.umc.movie.domain.exception.DuplicateMemberException;
import com.umc.movie.domain.repository.MemberRepository;
import com.umc.movie.global.error.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public NicknameCheckResponse checkNickname(String nickname) {

        if (nickname.length() < 2 || nickname.length() > 12) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "닉네임은 2~12자여야 합니다."
            );
        }

        boolean exists = memberRepository.existsByNickname(nickname);
        return new NicknameCheckResponse(!exists);
    }

    @Transactional(readOnly = true)
    public EmailCheckResponse checkEmail(String email) {

        if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "올바른 이메일 형식이 아닙니다."
            );
        }

        boolean exists = memberRepository.existsByEmail(email);
        return new EmailCheckResponse(!exists);
    }

    @Transactional
    public MemberResponse signUp(SignUpRequest request) {

        if (memberRepository.existsByEmail(request.email())) {
            throw new DuplicateMemberException(
                    "MEMBER4091",
                    "이미 사용 중인 이메일입니다."
            );
        }

        if (memberRepository.existsByNickname(request.nickname())) {
            throw new DuplicateMemberException(
                    "MEMBER4092",
                    "이미 사용 중인 닉네임입니다."
            );
        }

        Member member = Member.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .nickname(request.nickname())
                .build();

        Member savedMember = memberRepository.save(member);

        return new MemberResponse(
                savedMember.getMemberId(),
                savedMember.getEmail(),
                savedMember.getNickname()
        );
    }

    @Transactional(readOnly = true)
    public MemberInfoResponse getMyInfo(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        "MEMBER404_1",
                        "해당 회원을 찾을 수 없습니다."
                ));

        return MemberInfoResponse.from(member);
    }
}