package com.bi.barfdog.domain.member;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Grade {
    BRONZE, SILVER, GOLD, PLATINUM, DIAMOND, BARF;

    @JsonCreator
    public static Grade from(String str) {
        return Grade.valueOf(str.toUpperCase());
    }
}
