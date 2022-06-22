package com.bi.barfdog.api.couponDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CodeCouponRequestDto {

    private String code;
    private String password;
}
