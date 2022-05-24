package com.bi.barfdog.domain.event;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum EventStatus {
    LEAKED,HIDDEN;

    @JsonCreator
    public static EventStatus from(String str) {
        return EventStatus.valueOf(str.toUpperCase());
    }
}
