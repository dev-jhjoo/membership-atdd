package com.jhjoo.atdd.membership.service;

import com.jhjoo.atdd.membership.dto.MembershipDetailResponse;
import com.jhjoo.atdd.membership.dto.MembershipResponse;
import com.jhjoo.atdd.membership.enums.MembershipType;
import com.jhjoo.atdd.membership.enums.PointSaveType;
import com.jhjoo.atdd.membership.exception.MembershipErrorResult;
import com.jhjoo.atdd.membership.model.Membership;
import com.jhjoo.atdd.membership.exception.MembershipException;
import com.jhjoo.atdd.membership.dao.MembershipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MembershipService {

    private final MembershipRepository membershipRepository;
    private final PointServiceFactory pointServiceFactory;

    public MembershipResponse createMembership(
            final String userId, final MembershipType membershipType, final Integer point
    ){
        membershipRepository.findByUserIdAndMembershipType(userId, membershipType)
                .ifPresent((v)-> { throw new MembershipException(MembershipErrorResult.DUPLICATED_MEMBERSHIP_REGISTER); });

        final Membership membershipBuilder = Membership.builder()
                .userId(userId)
                .membershipType(membershipType)
                .point(point)
                .build();

        final Membership savedMembership = membershipRepository.save(membershipBuilder);

        return MembershipResponse.builder()
                .id(savedMembership.getId())
                .membershipType(savedMembership.getMembershipType())
                .build();
    }

    public List<MembershipDetailResponse> membershipListByUserId(final String userId){
        final List<Membership> membershipList = membershipRepository.findAllByUserId(userId);

        final List<MembershipDetailResponse> membershipResponseList = membershipList
                .stream().map(v -> MembershipDetailResponse.builder()
                        .id(v.getId())
                        .membershipType(v.getMembershipType())
                        .createdAt(v.getCreatedAt())
                        .build()).collect(Collectors.toList());

        return membershipResponseList;
    }

    public List<Membership> membershipList(){
        return membershipRepository.findAll();
    }

    public Membership membershipByIdAndUserId(Long membershipId, String userId){
        Membership membership = membershipRepository.findByIdAndUserId(membershipId, userId)
                .orElseThrow( () -> new MembershipException(MembershipErrorResult.MEMBERSHIP_NOT_FOUND) );
        return membership;
    }

    public void removeMembershipByIdAndUserId(Long membershipId, String userId) {
        Membership membership = membershipRepository.findById(membershipId)
                .orElseThrow(() -> new MembershipException(MembershipErrorResult.MEMBERSHIP_NOT_FOUND));

        if(!membership.getUserId().equals(userId)){
            throw new MembershipException(MembershipErrorResult.NOT_MEMBERSHIP_OWNER);
        }

        membershipRepository.deleteById(membershipId);
    }

    public void accumulateMembershipPoint(Long membershipId, String userId, int price) {
        final Membership membership = membershipRepository.findById(membershipId).orElseThrow(
                ()-> new MembershipException(MembershipErrorResult.MEMBERSHIP_NOT_FOUND)
        );

        if(!membership.getUserId().equals(userId)){
            throw new MembershipException(MembershipErrorResult.NOT_MEMBERSHIP_OWNER);
        }

        PointService pointService = pointServiceFactory.getPointService(PointSaveType.AMOUNT);

        int addedPoint = membership.getPoint() + pointService.calculateAmount(price);
        membership.setPoint(addedPoint);

        membershipRepository.save(membership);
    }
}
