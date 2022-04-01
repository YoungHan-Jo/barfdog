package com.bi.barfdog.repository;

import com.bi.barfdog.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {
    Optional<Member> findByEmail(String email);

    Optional<Member> findByMyRecommendationCode(String recommendCode);

    Optional<Member> findByPhoneNumber(String phoneNumber);
}
