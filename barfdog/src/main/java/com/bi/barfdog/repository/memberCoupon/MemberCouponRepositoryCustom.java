package com.bi.barfdog.repository.memberCoupon;

import com.bi.barfdog.api.orderDto.OrderSheetGeneralCouponDto;
import com.bi.barfdog.api.orderDto.OrderSheetSubsCouponDto;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.memberCoupon.MemberCoupon;

import java.util.List;

public interface MemberCouponRepositoryCustom {
    List<OrderSheetSubsCouponDto> findSubscribeCouponDtos(Member member);

    List<MemberCoupon> findByMemberAndCode(Member member, String code);

    List<OrderSheetGeneralCouponDto> findGeneralCouponsDto(Member member);

    List<MemberCoupon> findExpiredCoupon();
}
