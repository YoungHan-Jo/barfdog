package com.bi.barfdog.domain.review;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ReviewStatus {
    ALL,REQUEST,RETURN,APPROVAL,ADMIN; // 전체(사용x), 리뷰 심사요청, 리뷰요청 반려, 리뷰요청 수락, 관리자작성리뷰

    @JsonCreator
    public static ReviewStatus from(String str) {
        return ReviewStatus.valueOf(str.toUpperCase());
    }
}
