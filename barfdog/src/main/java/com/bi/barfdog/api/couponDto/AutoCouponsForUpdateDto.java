package com.bi.barfdog.api.couponDto;

import com.bi.barfdog.domain.coupon.DiscountType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AutoCouponsForUpdateDto {

    private Long id;
    private String name;
    private DiscountType discountType; // 할인 타입 [ FIXED_RATE, FLAT_RATE ]
    private int discountDegree; // 할인 정도 ( 원/% )
    private int availableMinPrice; // 사용가능한 최소 금액

}
