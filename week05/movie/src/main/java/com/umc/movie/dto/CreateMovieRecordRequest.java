package com.umc.movie.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateMovieRecordRequest(
        @NotNull @Positive Long movieId,
        @NotNull @Min(1) @Max(5) Byte score,
        String comment,
        @NotNull Boolean addToFavorites
) {}