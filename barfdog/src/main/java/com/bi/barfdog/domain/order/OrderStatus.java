package com.bi.barfdog.domain.order;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum OrderStatus {
    ALL,
    BEFORE_PAYMENT,
    HOLD, FAILED,
    PAYMENT_DONE,
    PRODUCING, DELIVERY_READY,
    DELIVERY_START,
    SELLING_CANCEL,
    CANCEL_REQUEST, CANCEL_DONE,
    RETURN_REQUEST, RETURN_DONE,
    EXCHANGE_REQUEST, EXCHANGE_DONE,
    CONFIRM;

    @JsonCreator
    public static OrderStatus from(String str) {
        return OrderStatus.valueOf(str.toUpperCase());
    }

}
