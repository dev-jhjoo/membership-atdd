package com.jhjoo.atdd.membership.dao;

import com.jhjoo.atdd.membership.enums.MembershipType;
import com.jhjoo.atdd.membership.model.Membership;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
public class MembershipRepositoryTest {

    @Autowired
    private MembershipRepository membershipRepository;

    @Test
    public void 멤버쉽등록(){
        // given
        final Membership membership = Membership.builder()
                .userId("userId")
                .membershipType(MembershipType.NAVER)
                .point(10000)
                .build();

        // when
        final Membership result = membershipRepository.save(membership);

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getUserId()).isEqualTo("userId");
        assertThat(result.getMembershipType()).isEqualTo(MembershipType.NAVER);
        assertThat(result.getPoint()).isEqualTo(10000);
    }

    @Test
    public void 멤버쉽등록그리고조회(){
        // given
        final Membership membership = Membership.builder()
                .userId("userId")
                .membershipType(MembershipType.NAVER)
                .point(10000)
                .build();

        // when
        membershipRepository.save(membership);
        final Membership result = membershipRepository.findByUserIdAndMembershipType("userId", MembershipType.NAVER).get();


        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getUserId()).isEqualTo("userId");
        assertThat(result.getMembershipType()).isEqualTo(MembershipType.NAVER);
        assertThat(result.getPoint()).isEqualTo(10000);
    }


    @Test
    public void 멤버십조회_사이즈0(){
        // givne

        // when
        List<Membership> membershipList = membershipRepository.findAllByUserId("userId");

        // then
        assertThat(membershipList.size()).isEqualTo(0);
    }

    @Test
    public void 멤버십조회_사이즈2(){
        // given
        final Membership naverMembership = Membership.builder()
                .userId("userId")
                .membershipType(MembershipType.NAVER)
                .point(10000)
                .build();
        final Membership kakaoMembership = Membership.builder()
                .userId("userId")
                .membershipType(MembershipType.KAKAO)
                .point(10000)
                .build();

        membershipRepository.save(naverMembership);
        membershipRepository.save(kakaoMembership);

        // when
        List<Membership> membershipList = membershipRepository.findAllByUserId("userId");

        // thne
        assertThat(membershipList.size()).isEqualTo(2);
    }

    @Test
    public void 멤버십추가후삭제(){
        // given
        final Membership naverMembership = Membership.builder()
                .userId("123")
                .membershipType(MembershipType.NAVER)
                .point(10000)
                .build();

        final Membership savedMembership = membershipRepository.save(naverMembership);

        // when
        membershipRepository.deleteById(savedMembership.getId());

        // then
        assertThat(membershipRepository.findById(savedMembership.getId()).isPresent()).isFalse();
    }

}
