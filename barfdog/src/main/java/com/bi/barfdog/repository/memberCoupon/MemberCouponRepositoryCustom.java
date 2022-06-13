package com.bi.barfdog.repository.memberCoupon;

import com.bi.barfdog.api.orderDto.OrderSheetSubsCouponDto;
import com.bi.barfdog.domain.member.Member;

import java.util.List;

public interface MemberCouponRepositoryCustom {
    List<OrderSheetSubsCouponDto> findSubscribeCouponDtos(Member member);
}
