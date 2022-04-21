package com.bi.barfdog.api.couponDto;

import com.bi.barfdog.domain.coupon.CouponTarget;
import com.bi.barfdog.domain.coupon.CouponType;
import com.bi.barfdog.domain.coupon.DiscountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CouponSaveRequestDto {

    @NotEmpty
    private String name;

    private String code;

    @NotEmpty
    private String description;

    @NotNull
    private int amount;

    @NotNull
    private DiscountType discountType; // 할인 타입 [ FIXED_RATE, FLAT_RATE ]

    @NotNull
    private int discountDegree;

    @NotNull
    private int availableMaxDiscount; // 적용가능 최대 할인금액

    @NotNull
    private int availableMinPrice; // 사용가능한 최소 금액

    @NotNull
    private CouponTarget couponTarget;

}
