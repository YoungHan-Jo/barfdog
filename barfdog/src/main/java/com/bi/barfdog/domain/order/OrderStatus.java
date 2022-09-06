package com.bi.barfdog.domain.order;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum OrderStatus {
    ALL,
    BEFORE_PAYMENT, CANCEL_PAYMENT,
    HOLD, FAILED, RESERVED_PAYMENT,
    PAYMENT_DONE,
    PRODUCING, DELIVERY_READY,
    DELIVERY_START,
    DELIVERY_DONE,
    SELLING_CANCEL,
    CANCEL_REQUEST, CANCEL_DONE_SELLER, CANCEL_DONE_BUYER,
    RETURN_REQUEST, RETURN_DONE_SELLER, RETURN_DONE_BUYER,
    EXCHANGE_REQUEST, EXCHANGE_DONE_SELLER, EXCHANGE_DONE_BUYER,
    CONFIRM;

    @JsonCreator
    public static OrderStatus from(String str) {
        return OrderStatus.valueOf(str.toUpperCase());
    }

}
