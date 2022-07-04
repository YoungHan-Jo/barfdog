package com.bi.barfdog.api.orderDto;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum OrderType {
    GENERAL,SUBSCRIBE;

    @JsonCreator
    public static OrderType from(String str) {
        return OrderType.valueOf(str.toUpperCase());
    }
}
