package com.bi.barfdog.api.subscribeDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UseCouponDto {

    @NotNull
    private Long memberCouponId;
    @NotNull
    private int discount;

}
