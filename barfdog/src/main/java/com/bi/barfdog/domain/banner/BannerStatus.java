package com.bi.barfdog.domain.banner;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Locale;

public enum BannerStatus {
    LEAKED, HIDDEN;

    @JsonCreator // api 요청 리퀘스트를 @RequestBody로 받을 때, 필요함
    public static BannerStatus from(String str) {
        return BannerStatus.valueOf(str.toUpperCase());
    }
}
