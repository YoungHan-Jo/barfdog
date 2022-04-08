package com.bi.barfdog.domain.dog;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum DogSize {
    LARGE, MIDDLE, SMALL;

    @JsonCreator
    public static DogSize from(String str) {
        return DogSize.valueOf(str.toUpperCase());
    }

}
