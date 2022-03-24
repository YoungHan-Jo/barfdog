package com.bi.barfdog.domain.banner;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum PopupBannerPosition {
    LEFT, MID, RIGHT;

    @JsonCreator
    public static PopupBannerPosition from(String str) {
        return PopupBannerPosition.valueOf(str.toUpperCase());
    }

}
