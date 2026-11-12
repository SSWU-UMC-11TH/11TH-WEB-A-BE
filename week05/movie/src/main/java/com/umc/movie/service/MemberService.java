package com.umc.movie.service;

import com.umc.movie.dto.*;
import com.umc.movie.entity.Member;
import com.umc.movie.exception.ApiException;
import com.umc.movie.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public AvailabilityResponse checkNickname(String nickname) {
        return new AvailabilityResponse(
                !memberRepository.existsByNickname(nickname)
        );
    }

    @Transactional(readOnly = true)
    public AvailabilityResponse checkEmail(String email) {
        return new AvailabilityResponse(
                !memberRepository.existsByEmail(email)
        );
    }

    @Transactional
    public MemberResponse signUp(SignUpRequest request) {
        if (memberRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        if (memberRepository.existsByNickname(request.nickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        Member member = Member.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .nickname(request.nickname())
                .build();

        Member savedMember = memberRepository.save(member);

        return new MemberResponse(
                savedMember.getId(),
                savedMember.getEmail(),
                savedMember.getNickname()
        );
    }

    @Transactional(readOnly = true)
    public MemberInfoResponse getMyInfo(Long memberId) {
        Member member = findMember(memberId);
        return MemberInfoResponse.from(member);
    }

    @Transactional
    public MemberInfoResponse updateMyInfo(
            Long memberId,
            MemberUpdateRequest request
    ) {
        Member member = findMember(memberId);

        if (memberRepository.existsByNicknameAndIdNot(
                request.nickname(), memberId
        )) {
            throw new ApiException(
                    HttpStatus.CONFLICT,
                    "MEMBER409_1",
                    "이미 사용 중인 닉네임입니다."
            );
        }

        member.updateNickname(request.nickname());

        return MemberInfoResponse.from(member);
    }

    private Member findMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        "MEMBER404_1",
                        "해당 회원을 찾을 수 없습니다."
                ));
    }
}