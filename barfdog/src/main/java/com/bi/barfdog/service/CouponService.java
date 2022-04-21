package com.bi.barfdog.service;

import com.bi.barfdog.api.couponDto.CouponSaveRequestDto;
import com.bi.barfdog.domain.coupon.Coupon;
import com.bi.barfdog.domain.coupon.CouponStatus;
import com.bi.barfdog.domain.coupon.CouponType;
import com.bi.barfdog.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CouponService {

    private final CouponRepository couponRepository;

    @Transactional
    public void createCoupon(CouponSaveRequestDto requestDto) {
        Coupon coupon = null;

        if (requestDto.getCode() == null || requestDto.getCode().length() == 0) { // 관리자 발행
            coupon = Coupon.builder()
                    .name(requestDto.getName())
                    .code("")
                    .couponType(CouponType.ADMIN_PUBLISHED)
                    .description(requestDto.getDescription())
                    .amount(requestDto.getAmount())
                    .discountType(requestDto.getDiscountType())
                    .discountDegree(requestDto.getDiscountDegree())
                    .availableMaxDiscount(requestDto.getAvailableMaxDiscount())
                    .availableMinPrice(requestDto.getAvailableMinPrice())
                    .couponTarget(requestDto.getCouponTarget())
                    .status(CouponStatus.ACTIVE)
                    .build();

        } else { // 코드 발행
            coupon = Coupon.builder()
                    .name(requestDto.getName())
                    .couponType(CouponType.CODE_PUBLISHED)
                    .code(requestDto.getCode())
                    .description(requestDto.getDescription())
                    .amount(requestDto.getAmount())
                    .discountType(requestDto.getDiscountType())
                    .discountDegree(requestDto.getDiscountDegree())
                    .availableMaxDiscount(requestDto.getAvailableMaxDiscount())
                    .availableMinPrice(requestDto.getAvailableMinPrice())
                    .couponTarget(requestDto.getCouponTarget())
                    .status(CouponStatus.ACTIVE)
                    .build();
        }

        couponRepository.save(coupon);
    }
}
