
package com.umc.movie.dto;

public record ApiResponse<T>(
        boolean isSuccess,
        String code,
        String message,
        T result
) {
    public static <T> ApiResponse<T> success(
            String code, String message, T result
    ) {
        return new ApiResponse<>(true, code, message, result);
    }

    public static <T> ApiResponse<T> error(
            String code, String message
    ) {
        return new ApiResponse<>(false, code, message, null);
    }
}
