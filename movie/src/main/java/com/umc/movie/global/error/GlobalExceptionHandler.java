package com.umc.movie.global.error;

import com.umc.movie.domain.exception.DuplicateMemberException;
import com.umc.movie.global.common.ApiResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(
            MethodArgumentNotValidException exception
    ) {
        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("잘못된 요청입니다.");

        return ResponseEntity.badRequest()
                .body(ApiResponse.failure("COMMON4001", message));
    }

    @ExceptionHandler(DuplicateMemberException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateMember(
            DuplicateMemberException exception
    ) {
        return ResponseEntity.status(409)
                .body(ApiResponse.failure(
                        exception.getCode(),
                        exception.getMessage()
                ));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrity(
            DataIntegrityViolationException exception
    ) {
        String message = exception.getMostSpecificCause().getMessage();
        String detail = message == null
                ? ""
                : message.toLowerCase(Locale.ROOT);

        if (detail.contains("uk_member_email")) {
            return ResponseEntity.status(409)
                    .body(ApiResponse.failure(
                            "MEMBER4091",
                            "이미 사용 중인 이메일입니다."
                    ));
        }

        if (detail.contains("uk_member_nickname")) {
            return ResponseEntity.status(409)
                    .body(ApiResponse.failure(
                            "MEMBER4092",
                            "이미 사용 중인 닉네임입니다."
                    ));
        }

        return ResponseEntity.status(409)
                .body(ApiResponse.failure(
                        "COMMON4091",
                        "데이터 제약 조건을 위반했습니다."
                ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(
            IllegalArgumentException exception
    ) {
        return ResponseEntity.status(409)
                .body(ApiResponse.failure(
                        "COMMON4092",
                        exception.getMessage()
                ));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse<Void>> handleResponseStatus(
            ResponseStatusException exception
    ) {
        int status = exception.getStatusCode().value();

        return ResponseEntity.status(status)
                .body(ApiResponse.failure(
                        "COMMON" + status,
                        exception.getReason()
                ));
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Void>> handleApiException(
            ApiException exception
    ) {
        return ResponseEntity.status(exception.getStatus())
                .body(ApiResponse.failure(
                        exception.getCode(),
                        exception.getMessage()
                ));
    }
}
