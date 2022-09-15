package com.bi.barfdog.domain.coupon;

import com.bi.barfdog.api.couponDto.UpdateAutoCouponRequest;
import com.bi.barfdog.domain.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.bi.barfdog.api.couponDto.UpdateAutoCouponRequest.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder @Getter
@Entity
public class Coupon extends BaseTimeEntity {
    // 쿠폰 객체
    // 쿠폰 객체는 유저가 보유한 쿠폰이 아님.
    // Member - memberCoupon - coupon 으로 일대다, 다대일로 풀어져있음
    // memberCoupon 객체가 유저가 보유한 쿠폰

    @Id @GeneratedValue
    @Column(name = "coupon_id")
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private CouponType couponType; // 쿠폰 타입 [AUTO_PUBLISHED, GENERAL_PUBLISHED, CODE_PUBLISHED], 각 자동/일반/코드 타입

    @Column(length = 15)
    private String code; // 쿠폰 코드, 쿠폰 코드가 없으면 일반 쿠폰, 코드가 있으면 코드형 쿠폰

    private String description; // 설명

    private LocalDateTime lastExpiredDate; // 만료 날짜

    private int amount; // 수량

    @Enumerated(EnumType.STRING)
    private DiscountType discountType; // 할인 타입 [ FIXED_RATE, FLAT_RATE ], 각 퍼센트/ 절대값

    private int discountDegree; // 할인 정도 ( discountType에 따라 % or 원 )

    private int availableMaxDiscount; // 적용가능 최대 할인금액

    private int availableMinPrice; // 적용가능한 최소 상품 금액

    @Enumerated(EnumType.STRING)
    private CouponTarget couponTarget; // 사용 가능 품목 대상 [ALL, GENERAL, SUBSCRIBE]

    @Enumerated(EnumType.STRING)
    private CouponStatus couponStatus; // [ACTIVE,INACTIVE]

    /*
    * 비즈니스 로직
    * */
    public void inactive() {
        couponStatus = CouponStatus.INACTIVE;
    }


    public void publish(LocalDateTime expiredDate) {
        if (lastExpiredDate == null) {
            lastExpiredDate = expiredDate;
        }else {
            if (lastExpiredDate.isBefore(expiredDate)) {
                lastExpiredDate = expiredDate;
            }
        }
    }

    public void updateAutoCoupon(UpdateAutoCouponRequestDto dto) {
        this.discountDegree = dto.getDiscountDegree();
        this.availableMinPrice = dto.getAvailableMinPrice();
    }
}
