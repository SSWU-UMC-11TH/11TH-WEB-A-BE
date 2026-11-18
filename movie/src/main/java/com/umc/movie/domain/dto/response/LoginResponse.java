package com.umc.movie.domain.dto.response;

public record LoginResponse(
        String nickname,
        String accessToken
) {
}
