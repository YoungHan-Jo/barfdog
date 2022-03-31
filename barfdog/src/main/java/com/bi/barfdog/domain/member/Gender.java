package com.bi.barfdog.domain.member;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Gender {
    MALE, FEMALE, NONE;

    @JsonCreator
    public static Gender from(String str){
        return Gender.valueOf(str.toUpperCase());
    }

}
