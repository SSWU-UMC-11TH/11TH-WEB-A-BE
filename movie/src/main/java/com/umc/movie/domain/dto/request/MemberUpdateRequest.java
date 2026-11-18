package com.umc.movie.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MemberUpdateRequest(
        @NotBlank(message = "닉네임을 입력해주세요.")
        @Size(min = 2, max = 12,
                message = "닉네임은 2~12자여야 합니다.")
        String nickname
) {
}
