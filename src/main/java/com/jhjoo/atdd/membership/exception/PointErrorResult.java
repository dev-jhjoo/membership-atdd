package com.jhjoo.atdd.membership.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PointErrorResult {
    UNKNOWN_SERVICE_TYPE(HttpStatus.BAD_REQUEST, "Unknown Point Service Type"),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
