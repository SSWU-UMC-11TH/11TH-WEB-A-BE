package com.umc.movie.domain.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateRatingRequest(

        @NotNull(message = "평점을 입력해주세요.")
        @Min(value = 1, message = "평점은 1점 이상이어야 합니다.")
        @Max(value = 5, message = "평점은 5점 이하여야 합니다.")
        Integer score,

        @Size(max = 500, message = "후기는 500자 이하로 입력해주세요.")
        @Pattern(regexp = "^(?!\\s+$).*$", message = "후기는 공백만 입력할 수 없습니다.")
        String comment
) {}