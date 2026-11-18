package com.umc.movie.domain.exception;

import lombok.Getter;

@Getter
public class DuplicateMemberException extends RuntimeException {

    private final String code;

    public DuplicateMemberException(String code, String message) {
        super(message);
        this.code = code;
    }
}
