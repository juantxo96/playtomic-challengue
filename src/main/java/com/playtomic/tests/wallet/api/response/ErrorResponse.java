package com.playtomic.tests.wallet.api.response;

import lombok.Getter;

@Getter
public enum ErrorResponse {
    UNEXPECTED_ERROR(1, "An unexpected error occurred.");

    private final Integer code;
    private final String message;

    ErrorResponse(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
