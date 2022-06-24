package com.bi.barfdog.repository.member;

import com.bi.barfdog.api.memberDto.FindEmailResponseDto;
import com.bi.barfdog.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long>, MemberRepositoryCustom {

    Optional<Member> findByEmail(String email);

    Optional<Member> findByMyRecommendationCode(String recommendCode);

    Optional<Member> findByPhoneNumber(String phoneNumber);

    @Query("select new com.bi.barfdog.api.memberDto.FindEmailResponseDto(m.email, m.provider)" +
            " from Member m where m.name = :name and m.phoneNumber = :phoneNumber")
    Optional<FindEmailResponseDto> findByNameAndPhoneNumber(@Param("name") String name,@Param("phoneNumber") String phoneNumber);

    Optional<Member> findByEmailAndNameAndPhoneNumber(String email, String name, String phoneNumber);

    Optional<Member> findByProviderAndProviderId(String provider, String providerId);
}
