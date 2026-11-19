package com.umc.movie.dto;

public record MovieRecordResponse(
        Long ratingId,
        Long movieId,
        Byte score,
        String comment,
        boolean addedToFavorites
) {}