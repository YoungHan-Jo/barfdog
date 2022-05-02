package com.bi.barfdog.api.couponDto;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Area {
    ALL, METRO, NON_METRO;

    @JsonCreator
    public static Area from(String str) {
        return Area.valueOf(str.toUpperCase());
    }
}
