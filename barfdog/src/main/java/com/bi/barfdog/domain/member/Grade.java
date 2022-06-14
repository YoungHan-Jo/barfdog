package com.bi.barfdog.domain.member;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Grade {
    브론즈, 실버, 골드, 플래티넘, 다이아몬드, 더바프;

    @JsonCreator
    public static Grade from(String str) {
        return Grade.valueOf(str.toUpperCase());
    }
}
