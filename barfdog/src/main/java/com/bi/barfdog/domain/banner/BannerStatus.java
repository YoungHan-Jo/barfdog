package com.bi.barfdog.domain.banner;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Locale;

public enum BannerStatus {
    LEAKED, HIDDEN;

    @JsonCreator
    public static BannerStatus from(String str) {
        for (BannerStatus value : BannerStatus.values()) {
            if (value.name().equals(str.toUpperCase())) {
                return value;
            }
        }
        return null;
    }
}
