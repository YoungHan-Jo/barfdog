package com.bi.barfdog.api.couponDto;

import com.bi.barfdog.domain.coupon.CouponStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryCouponsDto {

    private Long id;
    private CouponStatus status;
    private String name;
    private String description;
    private LocalDateTime expiredDate;
    private String discount;
    private int remaining;

}
