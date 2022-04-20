package com.bi.barfdog.domain.coupon;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum CouponTarget {
    ALL, GENERAL, SUBSCRIBE;

    @JsonCreator
    public static CouponTarget from(String str) {
        return CouponTarget.valueOf(str.toUpperCase());
    }
}
