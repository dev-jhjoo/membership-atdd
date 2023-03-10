package com.jhjoo.atdd.membership.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PointException extends RuntimeException{

    private final PointErrorResult errorResult;
}
