package com.bi.barfdog.repository;

import com.bi.barfdog.api.couponDto.CouponListResponseDto;
import com.bi.barfdog.domain.coupon.Coupon;

import java.util.List;

public interface CouponRepositoryCustom {

    List<CouponListResponseDto> findRedirectCouponsByKeyword(String keyword);
}
