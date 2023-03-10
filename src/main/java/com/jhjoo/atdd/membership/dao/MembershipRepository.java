package com.jhjoo.atdd.membership.dao;

import com.jhjoo.atdd.membership.enums.MembershipType;
import com.jhjoo.atdd.membership.model.Membership;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MembershipRepository extends JpaRepository<Membership, Long> {
    Optional<Membership> findByUserIdAndMembershipType(final String userId, final MembershipType membershipType);

    List<Membership> findAllByUserId(final String userId);

    Optional<Membership> findByIdAndUserId(Long id, String userId);



}
