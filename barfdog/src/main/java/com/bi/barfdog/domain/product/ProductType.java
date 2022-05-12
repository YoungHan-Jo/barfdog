package com.bi.barfdog.domain.product;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ProductType {
    FRESH, TOPPING, GOODS;

    @JsonCreator
    public static ProductType from(String str) {
        return ProductType.valueOf(str.toUpperCase());
    }
}
