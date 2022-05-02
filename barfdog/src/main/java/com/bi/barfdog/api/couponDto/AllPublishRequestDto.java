package com.bi.barfdog.api.couponDto;

import com.bi.barfdog.domain.coupon.CouponType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AllPublishRequestDto {

    @NotEmpty
    private String expiredDate;
    @NotNull
    private CouponType couponType;
    @NotNull
    private Long couponId;
    @NotNull
    private boolean alimTalk;
}
