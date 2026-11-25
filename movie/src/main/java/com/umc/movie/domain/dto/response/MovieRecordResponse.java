package com.umc.movie.domain.dto.response;

public record MovieRecordResponse(
        RatingResponse rating,
        FavoriteResponse favorite
) {
}