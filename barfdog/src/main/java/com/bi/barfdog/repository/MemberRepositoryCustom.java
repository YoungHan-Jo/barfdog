package com.bi.barfdog.repository;

import com.bi.barfdog.api.memberDto.MemberConditionPublishCoupon;
import com.bi.barfdog.api.memberDto.MemberPublishCouponResponseDto;
import com.bi.barfdog.domain.member.Member;

import java.util.List;

public interface MemberRepositoryCustom {
    List<MemberPublishCouponResponseDto> searchMemberDtosInPublication(MemberConditionPublishCoupon condition);

    List<Member> findByIdList(List<Long> memberIdList);
}
