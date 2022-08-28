package com.bi.barfdog.api.orderDto;

import com.bi.barfdog.domain.order.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubscribeOrderRequestDto {

    private Long memberCouponId; // 사용한 쿠폰 id

    @Valid
    private DeliveryDto deliveryDto;

    @NotNull
    private int orderPrice; // 주문 금액(구독상품 원가 - 등급할인)
    @NotNull
    private int deliveryPrice; // 배송비
    @NotNull
    private int discountTotal; // 할인 총합
    @NotNull
    private int discountReward; // 적립금 할인
    @NotNull
    private int discountCoupon; // 쿠폰 할인
    @NotNull
    private int discountGrade; // 쿠폰 할인
    @NotNull
    private int paymentPrice; // 결제 금액
    @NotNull
    private PaymentMethod paymentMethod; // [CREDIT_CARD, NAVER_PAY, KAKAO_PAY]
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate nextDeliveryDate;
    @NotNull
    private boolean isBrochure; // 브로슈어 받을 것인지
    @NotNull
    private boolean isAgreePrivacy; // 개정정보 동의

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class DeliveryDto {
        @NotEmpty
        private String name;
        @NotEmpty
        private String phone;
        @NotEmpty
        private String zipcode;
        @NotEmpty
        private String street;
        @NotEmpty
        private String detailAddress;

        private String request;
    }

}
