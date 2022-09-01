package com.bi.barfdog.api.orderDto;

import com.bi.barfdog.domain.order.OrderStatus;
import com.bi.barfdog.domain.order.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryAdminGeneralOrderDto {

    private OrderInfoDto orderInfoDto;
    @Builder.Default
    private List<OrderItemAndOptionDto> orderItemAndOptionDtoList = new ArrayList<>();
    private PaymentDto paymentDto;
    private DeliveryDto deliveryDto;

    @Data
    @AllArgsConstructor
    public static class OrderInfoDto {

        private Long id;
        private String merchantUid;
        private LocalDateTime orderDate;
        private String orderType;
        private boolean isPackage;

        private String memberName;
        private String phoneNumber;
        private String Email;
        private boolean isSubscribe;

    }

    @Data
    @AllArgsConstructor
    @Builder
    public static class OrderItemAndOptionDto {

        private OrderItemDto orderItemDto;
        @Builder.Default
        private List<SelectOptionDto> selectOptionDtoList = new ArrayList<>();

    }

    @Data
    @AllArgsConstructor
    public static class OrderItemDto{

        private Long orderItemId;
        private String itemName;
        private int amount; // 수량
        private int finalPrice; // 옵션 쿠폰 적용 최종 금액
        private String couponName; // 사용한 쿠폰 이름
        private int discountAmount; // 쿠폰 적용 할인 금액
        private OrderStatus status;

        private String cancelReason;
        private String cancelDetailReason;
        private LocalDateTime cancelRequestDate;
        private LocalDateTime cancelConfirmDate;

        private String returnReason;
        private String returnDetailReason;
        private LocalDateTime returnRequestDate;
        private LocalDateTime returnConfirmDate;

        private String exchangeReason;
        private String exchangeDetailReason;
        private LocalDateTime exchangeRequestDate;
        private LocalDateTime exchangeConfirmDate;

    }

    @Data
    @AllArgsConstructor
    public static class SelectOptionDto {

        private String optionName;
        private int price;
        private int amount;

    }

    @Data
    @AllArgsConstructor
    public static class PaymentDto {

        private int orderPrice; // 주문 가격
        private int deliveryPrice; // 배송비
        private int discountReward; // 사용한 적립금
        private int discountCoupon;
        private int paymentPrice; // 결제 금액
        private PaymentMethod paymentMethod;
        private OrderStatus orderStatus;

        private LocalDateTime orderConfirmDate;

    }

    @Data
    @AllArgsConstructor
    public static class DeliveryDto {

        private String recipientName;
        private String recipientPhone;
        private String zipcode;
        private String street;
        private String detailAddress;

        private LocalDateTime departureDate; // 출발일
        private LocalDateTime arrivalDate; // 도착일

        private String deliveryNumber;

        private String request; // 요청사항

    }










}
