package com.bi.barfdog.domain.coupon;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum CouponType {
    AUTO_PUBLISHED, ADMIN_PUBLISHED, CODE_PUBLISHED;

    @JsonCreator
    public static CouponType from(String str) {
        return CouponType.valueOf(str.toUpperCase());
    }
}
