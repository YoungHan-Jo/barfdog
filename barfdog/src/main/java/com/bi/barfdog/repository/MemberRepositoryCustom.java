package com.bi.barfdog.repository;

import com.bi.barfdog.api.memberDto.MemberConditionPublishCoupon;
import com.bi.barfdog.api.memberDto.MemberPublishCouponResponseDto;

import java.util.List;

public interface MemberRepositoryCustom {
    List<MemberPublishCouponResponseDto> searchMemberDtosInPublication(MemberConditionPublishCoupon condition);
}
