package com.bi.barfdog.domain.banner;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum BannerTargets {
    ALL, GUESTS, MEMBERS, SUBSCRIBERS;

    @JsonCreator
    public static BannerTargets from(String str) {
        return BannerTargets.valueOf(str.toUpperCase());
    }

}
