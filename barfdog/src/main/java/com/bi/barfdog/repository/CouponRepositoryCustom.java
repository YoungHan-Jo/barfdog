package com.bi.barfdog.repository;

import com.bi.barfdog.api.couponDto.CouponListResponseDto;
import com.bi.barfdog.api.couponDto.PublicationCouponDto;
import com.bi.barfdog.domain.coupon.Coupon;
import com.bi.barfdog.domain.coupon.CouponType;

import java.util.List;

public interface CouponRepositoryCustom {

    List<CouponListResponseDto> findRedirectCouponsByKeyword(String keyword);

    List<CouponListResponseDto> findAutoCouponsByKeyword(String keyword);

    List<PublicationCouponDto> findPublicationCouponDtosByCouponType(CouponType adminPublished);
}
