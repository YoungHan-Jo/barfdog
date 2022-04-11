package com.bi.barfdog.domain.subscribe;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum SubscribePlan {
    FULL,HALF,TOPPING;

    @JsonCreator
    public static SubscribePlan from(String str) {
        return SubscribePlan.valueOf(str.toUpperCase());
    }

}
