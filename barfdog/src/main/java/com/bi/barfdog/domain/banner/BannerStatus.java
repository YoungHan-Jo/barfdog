package com.bi.barfdog.domain.banner;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Locale;

public enum BannerStatus {
    LEAKED, HIDDEN;

    @JsonCreator
    public static BannerStatus from(String str) {
        return BannerStatus.valueOf(str.toUpperCase());
    }
}
