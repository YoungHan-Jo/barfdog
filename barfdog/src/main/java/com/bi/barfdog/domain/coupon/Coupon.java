package com.bi.barfdog.domain.coupon;

import com.bi.barfdog.domain.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder @Getter
@Entity
public class Coupon extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "coupon_id")
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private CouponType couponType; // 쿠폰 타입 [AUTO_PUBLISHED, ADMIN_PUBLISHED, CODE_PUBLISHED]

    @Column(length = 15)
    private String code; // 쿠폰 코드

    private String description; // 설명

    private LocalDateTime expiredDate; // 마지막으로 발행된 쿠폰 유효기간

    private int amount; // 수량

    @Enumerated(EnumType.STRING)
    private DiscountType discountType; // 할인 타입 [ FIXED_RATE, FLAT_RATE ]

    private int discountDegree; // 할인 정도 ( 원/% )

    private int availableMaxDiscount; // 적용가능 최대 할인금액

    private int availableMinPrice; // 사용가능한 최소 금액

    @Enumerated(EnumType.STRING)
    private CouponTarget couponTarget; // 사용 가능 품목 대상 [ALL, GENERAL, SUBSCRIBE]

    @Enumerated(EnumType.STRING)
    private CouponStatus status; // [ACTIVE,INACTIVE]

    /*
    * 비지니스 로직
    * */
    public void inactive() {
        status = CouponStatus.INACTIVE;
    }
}
