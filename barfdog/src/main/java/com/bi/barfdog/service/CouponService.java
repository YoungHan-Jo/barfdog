package com.bi.barfdog.service;

import com.bi.barfdog.api.couponDto.CouponSaveRequestDto;
import com.bi.barfdog.api.couponDto.PersonalPublishRequestDto;
import com.bi.barfdog.domain.coupon.Coupon;
import com.bi.barfdog.domain.coupon.CouponStatus;
import com.bi.barfdog.domain.coupon.CouponType;
import com.bi.barfdog.domain.memberCoupon.MemberCoupon;
import com.bi.barfdog.repository.CouponRepository;
import com.bi.barfdog.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CouponService {

    private final CouponRepository couponRepository;
    private final MemberRepository memberRepository;
    private final EntityManager em;

    @Transactional
    public void createCoupon(CouponSaveRequestDto requestDto) {
        Coupon coupon = null;

        if (requestDto.getCode() == null || requestDto.getCode().length() == 0) { // 관리자 발행
            coupon = Coupon.builder()
                    .name(requestDto.getName())
                    .code("")
                    .couponType(CouponType.GENERAL_PUBLISHED)
                    .description(requestDto.getDescription())
                    .amount(requestDto.getAmount())
                    .discountType(requestDto.getDiscountType())
                    .discountDegree(requestDto.getDiscountDegree())
                    .availableMaxDiscount(requestDto.getAvailableMaxDiscount())
                    .availableMinPrice(requestDto.getAvailableMinPrice())
                    .couponTarget(requestDto.getCouponTarget())
                    .couponStatus(CouponStatus.ACTIVE)
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
                    .couponStatus(CouponStatus.ACTIVE)
                    .build();
        }

        couponRepository.save(coupon);
    }

    @Transactional
    public void inactiveCoupon(Long id) {
        Coupon coupon = couponRepository.findById(id).get();

        coupon.inactive();
    }

    @Transactional
    public void publishCouponsToPersonal(PersonalPublishRequestDto requestDto) {

        em.flush();
        em.clear();

//        MemberCoupon.builder()
//                .member()
//                .build();

    }
}
