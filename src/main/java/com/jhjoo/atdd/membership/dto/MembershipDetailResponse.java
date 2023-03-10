package com.jhjoo.atdd.membership.dto;

import com.jhjoo.atdd.membership.enums.MembershipType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString
public class MembershipDetailResponse {
    private Long id;
    private MembershipType membershipType;
    private Integer point;
    private LocalDateTime createdAt;
}
