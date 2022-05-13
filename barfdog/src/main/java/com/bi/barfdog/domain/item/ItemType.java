package com.bi.barfdog.domain.item;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ItemType {
    FRESH, TOPPING, GOODS;

    @JsonCreator
    public static ItemType from(String str) {
        return ItemType.valueOf(str.toUpperCase());
    }
}
