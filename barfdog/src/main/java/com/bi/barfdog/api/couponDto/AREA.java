package com.bi.barfdog.api.couponDto;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum AREA {
    ALL, METRO, NON_METRO;

    @JsonCreator
    public static AREA from(String str) {
        return AREA.valueOf(str.toUpperCase());
    }
}
