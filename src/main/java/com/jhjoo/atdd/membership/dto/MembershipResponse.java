package com.jhjoo.atdd.membership.dto;

import com.jhjoo.atdd.membership.enums.MembershipType;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@RequiredArgsConstructor
@ToString
public class MembershipResponse {
    private final Long id;
    private final MembershipType membershipType;
}
