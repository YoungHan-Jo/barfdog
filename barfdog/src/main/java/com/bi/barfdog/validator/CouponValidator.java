package com.bi.barfdog.validator;

import com.bi.barfdog.api.couponDto.CouponSaveRequestDto;
import com.bi.barfdog.api.couponDto.PersonalPublishRequestDto;
import com.bi.barfdog.domain.coupon.Coupon;
import com.bi.barfdog.domain.coupon.DiscountType;
import com.bi.barfdog.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class CouponValidator {

    private final CouponRepository couponRepository;

    public void validateDto(CouponSaveRequestDto requestDto, Errors errors) {
        if (requestDto.getDiscountDegree() > requestDto.getAvailableMaxDiscount()) {
            errors.reject("Wrong Input","할인금액은 최대사용금액보다 높을 수 없습니다.");
        }

        if (requestDto.getDiscountType() == DiscountType.FIXED_RATE) {
            if (requestDto.getDiscountDegree() >= 100) {
                errors.reject("Wrong Input","할인율은 100보다 높을 수 없습니다.");
            }
        }

    }

    public void validateDuplicate(CouponSaveRequestDto requestDto, Errors errors) {
        String code = requestDto.getCode();
        if (code != null && code.length() > 0) {
            Optional<Coupon> optionalCoupon = couponRepository.findByCode(code);
            if (optionalCoupon.isPresent()) {
                errors.reject("duplicate code","이미 존재하는 쿠폰 코드입니다.");
            }
        }
    }

    public void validateCouponType(PersonalPublishRequestDto requestDto, Errors errors) {
        Coupon findCoupon = couponRepository.findById(requestDto.getCouponId()).get();
        if (findCoupon.getCouponType() != requestDto.getCouponType()) {
            errors.reject("wrong couponType","쿠폰타입과 선택한 쿠폰의 쿠폰타입이 일치하지 않습니다.");
        }
    }
}
