package com.bi.barfdog.domain.blog;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum BlogStatus {
    LEAKED, HIDDEN;

    @JsonCreator
    public static BlogStatus from(String str) {
        return BlogStatus.valueOf(str.toUpperCase());
    }
}
