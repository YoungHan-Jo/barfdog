package com.bi.barfdog.validator;

import com.bi.barfdog.api.couponDto.CouponSaveRequestDto;
import com.bi.barfdog.api.couponDto.GroupPublishRequestDto;
import com.bi.barfdog.api.couponDto.PersonalPublishRequestDto;
import com.bi.barfdog.api.couponDto.PublishRequestDto;
import com.bi.barfdog.domain.coupon.Coupon;
import com.bi.barfdog.domain.coupon.DiscountType;
import com.bi.barfdog.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    public void validateCouponType(GroupPublishRequestDto requestDto, Errors errors) {
        Coupon findCoupon = couponRepository.findById(requestDto.getCouponId()).get();
        if (findCoupon.getCouponType() != requestDto.getCouponType()) {
            errors.reject("wrong couponType","쿠폰타입과 선택한 쿠폰의 쿠폰타입이 일치하지 않습니다.");
        }
    }

    public void validateBirthYear(GroupPublishRequestDto requestDto, Errors errors) {
        Integer from = Integer.valueOf(requestDto.getBirthYearFrom());
        Integer to = Integer.valueOf(requestDto.getBirthYearTo());
        if (from > to) {
            errors.reject("birthYear is Wrong","생년 범위의 순서가 잘못 되었습니다.");
        }
    }

    public void validateExpiredDate(GroupPublishRequestDto requestDto, Errors errors) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate expiredDate = LocalDate.parse(requestDto.getExpiredDate(), dateTimeFormatter);

        if (expiredDate.isBefore(LocalDate.now())) {
            errors.reject("expiredDate is passed","지정할 수 없는 유효기간 날짜입니다.");
        }
    }
}
