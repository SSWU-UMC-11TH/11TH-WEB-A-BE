package com.umc.movie.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateRatingRequest(
        @Schema(
                description = "평점",
                example = "5"
        )
        @NotNull(message = "평점을 입력해주세요.")
        @Min(value = 1, message = "평점은 1점 이상이어야 합니다.")
        @Max(value = 5, message = "평점은 5점 이하여야 합니다.")
        Integer score,

        @Schema(
                description = "후기",
                example = "정말 재미있는 영화였어요!"
        )
        @Size(max = 500, message = "후기는 500자 이하로 입력해주세요.")
        @Pattern(regexp = "^(?!\\s+$).*$", message = "후기는 공백만 입력할 수 없습니다.")
        String comment
) {}