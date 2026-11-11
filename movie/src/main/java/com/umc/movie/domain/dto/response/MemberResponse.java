package com.umc.movie.domain.dto.response;

public record MemberResponse(
        Long memberId,
        String email,
        String nickname
) {}
