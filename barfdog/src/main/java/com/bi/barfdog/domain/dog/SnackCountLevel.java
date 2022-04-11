package com.bi.barfdog.domain.dog;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum SnackCountLevel {

    LITTLE, NORMAL, MUCH;

    @JsonCreator
    public static SnackCountLevel from(String str){
        return SnackCountLevel.valueOf(str.toUpperCase());
    }

}
