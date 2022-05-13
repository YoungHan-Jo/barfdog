package com.bi.barfdog.domain.item;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ItemStatus {
    LEAKED,HIDDEN;

    @JsonCreator
    public static ItemStatus from(String str) {
        return ItemStatus.valueOf(str.toUpperCase());
    }
}
