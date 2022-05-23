package com.bi.barfdog.domain.item;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ItemType {
    RAW, TOPPING, GOODS;

    @JsonCreator
    public static ItemType from(String str) {
        return ItemType.valueOf(str.toUpperCase());
    }
}
