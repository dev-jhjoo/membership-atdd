package com.jhjoo.atdd.membership.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MembershipType {

    NAVER("naver"),
    LINE("line"),
    KAKAO("kakao"),
    ;

    private final String companyName;
}
