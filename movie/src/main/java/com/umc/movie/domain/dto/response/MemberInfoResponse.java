package com.umc.movie.domain.dto.response;

import com.umc.movie.domain.entity.Member;
import java.time.LocalDateTime;

public record MemberInfoResponse(
        Long memberId,
        String email,
        String nickname,
        LocalDateTime createdAt
) {
    public static MemberInfoResponse from(Member member) {
        return new MemberInfoResponse(
                member.getMemberId(),
                member.getEmail(),
                member.getNickname(),
                member.getCreatedAt()
        );
    }
}
