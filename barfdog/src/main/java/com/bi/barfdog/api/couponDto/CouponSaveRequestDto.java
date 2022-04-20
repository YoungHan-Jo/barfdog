package com.bi.barfdog.api.couponDto;

import com.bi.barfdog.domain.coupon.CouponTarget;
import com.bi.barfdog.domain.coupon.CouponType;
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
    @NotEmpty
    private int amount;
    @NotEmpty
    private int life;
    @NotNull
    private CouponType couponType;
    @NotEmpty
    private int discountDegree;
    @NotEmpty
    private int availableMaxDiscountAmount;
    @NotEmpty
    private int availableMinPrice;
    @NotNull
    private CouponTarget couponTarget;
    @NotEmpty
    private int remaining;

}
