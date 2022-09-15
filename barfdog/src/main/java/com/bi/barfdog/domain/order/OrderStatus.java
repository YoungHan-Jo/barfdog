package com.bi.barfdog.domain.order;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum OrderStatus {
    ALL,
    BEFORE_PAYMENT, CANCEL_PAYMENT, // 결제전, 결제취소(카드정보 작성중 취소 누른경우)
    HOLD, FAILED, RESERVED_PAYMENT, // 보류, 실패(카드결제 실패), 예약결제(아임포트 스케쥴걸린상태)
    PAYMENT_DONE, // 결제완료
    PRODUCING, DELIVERY_READY, // 생산중(구독주문), 배송준비(일반주문)
    DELIVERY_START, // 배송 출발
    DELIVERY_DONE, // 배송 도착 완료
    CANCEL_REQUEST, CANCEL_DONE_SELLER, CANCEL_DONE_BUYER, // 주문 취소 요청, 판매자귀책 취소완료, 구매자귀책 취소완료
    RETURN_REQUEST, RETURN_DONE_SELLER, RETURN_DONE_BUYER, // 주문 반품 요청, 판매자귀책 반품완료, 구매자귀책 반품완료
    EXCHANGE_REQUEST, EXCHANGE_DONE_SELLER, EXCHANGE_DONE_BUYER, // 주문 교환요청, 판매자귀책 교환완료, 구매자귀책 교환완료
    CONFIRM; // 구매확정

    @JsonCreator
    public static OrderStatus from(String str) {
        return OrderStatus.valueOf(str.toUpperCase());
    }

}
