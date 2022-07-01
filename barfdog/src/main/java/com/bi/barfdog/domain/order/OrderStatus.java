package com.bi.barfdog.domain.order;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum OrderStatus {
    ALL, HOLD, PAYMENT_DONE, PRODUCING,
    DELIVERY_READY, DELIVERY_START,
    SELLING_CANCEL, CANCEL_REQUEST, CANCEL_DONE,
    RETURN_REQUEST, RETURN_DONE,
    EXCHANGE_REQUEST, EXCHANGE_DONE,
    FAILED, CONFIRM;

    @JsonCreator
    public static OrderStatus from(String str) {
        return OrderStatus.valueOf(str.toUpperCase());
    }

}
