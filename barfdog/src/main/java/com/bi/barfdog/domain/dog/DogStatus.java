package com.bi.barfdog.domain.dog;


import com.fasterxml.jackson.annotation.JsonCreator;

public enum DogStatus {
    HEALTHY, NEED_DIET, OBESITY, PREGNANT, LACTATING;

    @JsonCreator
    public static DogStatus from(String str){
        return DogStatus.valueOf(str.toUpperCase());
    }

}
