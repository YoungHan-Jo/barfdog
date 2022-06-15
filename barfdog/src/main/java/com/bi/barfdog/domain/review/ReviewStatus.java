package com.bi.barfdog.domain.review;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ReviewStatus {
    ALL,REQUEST,RETURN,APPROVAL,ADMIN;

    @JsonCreator
    public static ReviewStatus from(String str) {
        return ReviewStatus.valueOf(str.toUpperCase());
    }
}
