package com.bi.barfdog.api.orderDto;

import com.bi.barfdog.domain.order.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GeneralOrderRequestDto {

    @Builder.Default
    private List<OrderItemDto> orderItemDtoList = new ArrayList<>();

    private DeliveryDto deliveryDto;

    private Long deliveryId; // 묶음 배송 할 배송 id
    @NotNull
    private int orderPrice; // 주문 금액
    @NotNull
    private int deliveryPrice; // 배송비
    @NotNull
    private int discountTotal; // 할인 총합
    @NotNull
    private int discountReward; // 적립금 할인
    @NotNull
    private int discountCoupon; // 쿠폰 할인
    @NotNull
    private int paymentPrice; // 결제 금액

    private PaymentMethod paymentMethod; // [CREDIT_CARD, NAVER_PAY, KAKAO_PAY]
    private boolean isBrochure; // 브로슈어 받을 것인지
    private boolean isAgreePrivacy; // 개정정보 동의

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class OrderItemDto {

        private Long itemId;
        private int amount;

        @Builder.Default
        private List<SelectOptionDto> selectOptionDtoList = new ArrayList<>();

        private Long memberCouponId;

        private int discountAmount; // 쿠폰으로 할인된 금액

        private int finalPrice; // 옵션포함 쿠폰적용 최종가격

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SelectOptionDto {

        private Long itemOptionId;
        private int amount;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class DeliveryDto {

        private String name;

        private String phone;

        private String zipcode;

        private String street;

        private String detailAddress;

        private String request;
    }

}
