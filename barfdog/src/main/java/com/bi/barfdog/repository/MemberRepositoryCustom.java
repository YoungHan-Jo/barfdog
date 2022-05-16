package com.bi.barfdog.repository;

import com.bi.barfdog.api.couponDto.GroupPublishRequestDto;
import com.bi.barfdog.api.memberDto.*;
import com.bi.barfdog.domain.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface MemberRepositoryCustom {
    List<MemberPublishCouponResponseDto> searchMemberDtosInPublication(MemberConditionPublishCoupon condition);

    List<Member> findByIdList(List<Long> memberIdList);

    List<Member> findMembersByGroupCouponCond(GroupPublishRequestDto requestDto);

    Page<QueryMembersDto> findDtosByCond(Pageable pageable, QueryMembersCond cond);

    Optional<QueryMemberDto> findMemberDto(Long id);
}
