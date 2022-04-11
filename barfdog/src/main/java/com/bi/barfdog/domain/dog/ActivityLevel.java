package com.bi.barfdog.domain.dog;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ActivityLevel {
    VERY_LITTLE, LITTLE, NORMAL, MUCH, VERY_MUCH;

    @JsonCreator
    public static ActivityLevel from(String str){
        return ActivityLevel.valueOf(str.toUpperCase());
    }


}
