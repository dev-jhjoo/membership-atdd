package com.jhjoo.atdd.membership.controller;

import com.jhjoo.atdd.membership.dto.MembershipDetailResponse;
import com.jhjoo.atdd.membership.dto.MembershipRequest;
import com.jhjoo.atdd.membership.dto.MembershipResponse;
import com.jhjoo.atdd.membership.model.Membership;
import com.jhjoo.atdd.membership.service.MembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.xml.ws.Response;

import java.util.List;

import static com.jhjoo.atdd.membership.enums.MembershipConstants.USER_ID_HEADER;

@RestController
@RequiredArgsConstructor
public class MembershipController {

    private final MembershipService membershipService;

    @PostMapping("/api/v1/memberships")
    public ResponseEntity<MembershipResponse> createMembership(
            @RequestHeader(USER_ID_HEADER) final String userId,
            @RequestBody @Valid final MembershipRequest membershipRequest){

        final MembershipResponse membershipResponse = membershipService.createMembership(userId, membershipRequest.getMembershipType(), membershipRequest.getPoint());

        return ResponseEntity.status(HttpStatus.CREATED).body(membershipResponse);
    }

    @GetMapping("/api/v1/memberships")
    public ResponseEntity<List<MembershipDetailResponse>> membershipList(
            @RequestHeader(USER_ID_HEADER) final String userId
    ){
        return ResponseEntity.status(HttpStatus.OK).body(membershipService.membershipListByUserId(userId));
    }


    @DeleteMapping("/api/v1/memverships/{id}")
    public ResponseEntity<Void> removeMembership(
            @RequestHeader(USER_ID_HEADER) final String userId,
            @PathVariable final Long id
    ){
        membershipService.removeMembershipByIdAndUserId(id, userId);
        return ResponseEntity.noContent().build();
    }

}
