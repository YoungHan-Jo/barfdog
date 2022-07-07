package com.bi.barfdog.api.couponDto;

import com.bi.barfdog.domain.coupon.CouponTarget;
import com.bi.barfdog.domain.coupon.CouponType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CouponListResponseDto {

    private Long id;

    private String name;

    private CouponType couponType;

    private String code;

    private String description;

    private String discount;

    private CouponTarget couponTarget;

    private int amount;

    private LocalDateTime expiredDate;

}
