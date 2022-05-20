package com.bi.barfdog.domain.blog;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum BlogCategory {
    NUTRITION,HEALTH,LIFE,NOTICE;

    @JsonCreator
    public static BlogCategory from(String str) {
        return BlogCategory.valueOf(str.toUpperCase());
    }
}
