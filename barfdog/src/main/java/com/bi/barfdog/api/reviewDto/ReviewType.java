package com.bi.barfdog.api.reviewDto;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ReviewType {
    ITEM,SUBSCRIBE;

    @JsonCreator
    private static ReviewType from(String str) {
        return ReviewType.valueOf(str.toUpperCase());
    }
}
