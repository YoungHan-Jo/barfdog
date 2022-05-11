package com.bi.barfdog.api.orderDto;

import com.bi.barfdog.domain.coupon.DiscountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderSheetSubsCouponDto {

    private Long memberCouponId;

    private String name;
    private DiscountType discountType;
    private int discountDegree;

    private int availableMaxDiscount;
    private int availableMinPrice;

    private int remaining;
    private LocalDateTime expiredDate;

}
