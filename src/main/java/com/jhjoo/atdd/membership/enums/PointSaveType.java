package com.jhjoo.atdd.membership.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PointSaveType {

    AMOUNT("AMOUNT"),
    PERCENT("PERCENT"),
    ;

    private final String type;
}
