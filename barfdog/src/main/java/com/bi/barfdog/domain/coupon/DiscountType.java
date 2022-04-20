package com.bi.barfdog.domain.coupon;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum DiscountType {
    FIXED_RATE, FLAT_RATE;

    @JsonCreator
    public static DiscountType from(String str) {
        return DiscountType.valueOf(str.toUpperCase());
    }
}
