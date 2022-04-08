package com.bi.barfdog.domain.recipe;

import com.bi.barfdog.domain.member.Gender;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum Leaked {
    LEAKED,HIDDEN;

    @JsonCreator
    public static Leaked from(String str){
        return Leaked.valueOf(str.toUpperCase());
    }

}
