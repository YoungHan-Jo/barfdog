package com.bi.barfdog.domain.banner;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum BannerTargets {
    ALL, GUESTS, MEMBERS, SUBSCRIBERS;

    @JsonCreator
    public static BannerTargets from(String str) {
        return BannerTargets.valueOf(str.toUpperCase());
//        for (BannerTargets value : BannerTargets.values()) {
//            if (value.name().equals(str.toUpperCase())) {
//                return value;
//            }
//        }
//        return null;
    }

}
