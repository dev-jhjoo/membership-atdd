package com.jhjoo.atdd.membership.service;

import com.jhjoo.atdd.membership.dto.MembershipDetailResponse;
import com.jhjoo.atdd.membership.dto.MembershipResponse;
import com.jhjoo.atdd.membership.enums.MembershipType;
import com.jhjoo.atdd.membership.enums.PointSaveType;
import com.jhjoo.atdd.membership.exception.MembershipErrorResult;
import com.jhjoo.atdd.membership.model.Membership;
import com.jhjoo.atdd.membership.exception.MembershipException;
import com.jhjoo.atdd.membership.dao.MembershipRepository;
import org.assertj.core.api.AssertionsForClassTypes;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MembershipServiceTest {

    @InjectMocks
    private MembershipService target;
    @Mock
    private PointServiceFactory serviceFactory;
    @Mock
    private MembershipRepository membershipRepository;

    private final Long membershipId = 1L;
    private final String userId = "userId";
    private final MembershipType membershipType = MembershipType.NAVER;
    private final Integer point = 10000;

    @Test
    public void 멤버십등록실패_이미존재(){
        // given
        doReturn(Optional.of(membership()))
                .when(membershipRepository)
                .findByUserIdAndMembershipType(userId, MembershipType.KAKAO);

        // when
        final MembershipException result =  assertThrows(
                MembershipException.class,
                () -> target.createMembership(userId, MembershipType.KAKAO, 10000)
        );

        // then
        assertThat(result.getErrorResult())
                .isEqualTo(MembershipErrorResult.DUPLICATED_MEMBERSHIP_REGISTER);

    }

    @Test
    public void 멤버십등록성공(){
        // given
        doReturn(Optional.empty()).when(membershipRepository)
                .findByUserIdAndMembershipType(userId,membershipType);
        doReturn(Membership.builder()
                .id(-1L)
                .userId(userId)
                .point(point)
                .membershipType(MembershipType.NAVER)
                .build()
        ).when(membershipRepository).save(any(Membership.class));

        // when
        final MembershipResponse membership = target.createMembership(userId, membershipType, point);

        // then
        assertThat(membership.getId()).isNotNull();
        AssertionsForClassTypes.assertThat(membership.getMembershipType()).isEqualTo(MembershipType.NAVER);


        // verify
        verify(membershipRepository, times(1)).findByUserIdAndMembershipType(userId, membershipType);
        verify(membershipRepository, times(1)).save(any(Membership.class));

    }

    @Test
    public void 멤버십목록조회(){
        // given
        doReturn(Arrays.asList(
                Membership.builder().build(),
                Membership.builder().build(),
                Membership.builder().build()
        )).when(membershipRepository).findAllByUserId(userId);

        // when
        final List<MembershipDetailResponse> membershipList = target.membershipListByUserId(userId);

        // then
        assertThat(membershipList.size()).isEqualTo(3);
    }


    @Test
    public void 멤버십상세조회실패_존재하지않음(){
        // given
        doReturn(Optional.empty()).when(membershipRepository).findByIdAndUserId(membershipId, userId);

        // when
        final MembershipException exception = assertThrows(
                MembershipException.class,
                () -> target.membershipByIdAndUserId(membershipId, userId)
        );

        // then
        assertThat(exception.getErrorResult()).isEqualTo(MembershipErrorResult.MEMBERSHIP_NOT_FOUND);
    }

    @Test
    public void 멤버십상세조회성공(){
        // given
        doReturn(Optional.of(membership())).when(membershipRepository).findByIdAndUserId(membershipId, userId);

        // when
        final Membership findMembership = target.membershipByIdAndUserId(membershipId, userId);

        // then
        assertThat(findMembership.getMembershipType()).isEqualTo(MembershipType.KAKAO);
        assertThat(findMembership.getPoint()).isEqualTo(10000);
    }

    @Test
    public void 멤버십삭제실패_존재하지않음(){
        // given
        doReturn(Optional.empty())
                .when(membershipRepository)
                .findById(membershipId);

        // when
        final MembershipException exception = assertThrows(
                MembershipException.class,
                () -> target.removeMembershipByIdAndUserId(membershipId, userId)
        );

        // then
        assertThat(exception.getErrorResult())
                .isEqualTo(MembershipErrorResult.MEMBERSHIP_NOT_FOUND);
    }

    @Test
    public void 멤버십삭제실패_본인이아님(){
        // given
        doReturn(Optional.of(membership()))
                .when(membershipRepository)
                .findById(membershipId);

        // when
        final MembershipException exception = assertThrows(
                MembershipException.class,
                () -> target.removeMembershipByIdAndUserId(membershipId, "empty")
        );

        // then
        assertThat(exception.getErrorResult())
                .isEqualTo(MembershipErrorResult.NOT_MEMBERSHIP_OWNER);
    }

    @Test
    public void 멤버십삭제성공(){
        // given
        doReturn(Optional.of(membership()))
                .when(membershipRepository)
                .findById(membershipId);

        // when
        target.removeMembershipByIdAndUserId(membershipId, userId);
        MembershipException exception = assertThrows(
                MembershipException.class,
                () -> target.membershipByIdAndUserId(membershipId, userId)
        );

        // then
        assertThat(exception.getErrorResult())
                .isEqualTo(MembershipErrorResult.MEMBERSHIP_NOT_FOUND);
    }

    @Test
    public void 멤버십적립실패_존재하지않음(){
        // given
        doReturn(Optional.empty())
                .when(membershipRepository)
                .findById((membershipId));
        // when
        final MembershipException exception = assertThrows(
                MembershipException.class,
                () -> target.accumulateMembershipPoint(membershipId, userId, 10000)
        );

        // then
        assertThat(exception.getErrorResult()).isEqualTo(MembershipErrorResult.MEMBERSHIP_NOT_FOUND);
    }


    @Test
    public void 멤버십적립성공(){
        // given
        doReturn(Optional.of(membership())).when(membershipRepository).findById(membershipId);
        doReturn(new AmountPointService()).when(serviceFactory).getPointService(PointSaveType.AMOUNT);

        // when
        target.accumulateMembershipPoint(membershipId, userId, 10000);

        // then
        final Membership afterMembership = membershipRepository.findById(membershipId).get();
        assertThat(afterMembership.getPoint()).isEqualTo(10100);
    }


    private Membership membership() {
        return Membership.builder()
                .id(membershipId)
                .userId(userId)
                .membershipType(MembershipType.KAKAO)
                .point(10000)
                .build();
    }


}