package com.bi.barfdog.domain.member;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Grade { // 이건 프론트쪽에서 한글로 돼냐해서 한글로 해봤는데 자동완성 안되서 불편 ㅋㅋ
    브론즈, 실버, 골드, 플래티넘, 다이아몬드, 더바프;

    @JsonCreator
    public static Grade from(String str) {
        return Grade.valueOf(str.toUpperCase());
    }
}
