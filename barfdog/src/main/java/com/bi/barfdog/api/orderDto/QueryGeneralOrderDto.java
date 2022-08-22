package com.bi.barfdog.api.orderDto;

import com.bi.barfdog.domain.order.OrderStatus;
import com.bi.barfdog.domain.order.PaymentMethod;
import com.bi.barfdog.domain.orderItem.OrderCancel;
import com.bi.barfdog.domain.orderItem.OrderExchange;
import com.bi.barfdog.domain.orderItem.OrderReturn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embedded;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryGeneralOrderDto {

    @Builder.Default
    private List<OrderItemDto> orderItemDtoList = new ArrayList<>();

    private OrderDto orderDto;

    private int savedRewardTotal;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class OrderItemDto {
        private Long orderItemId;
        private String thumbnailUrl;
        @Builder.Default
        private List<SelectOptionDto> selectOptionDtoList = new ArrayList<>();
        private String itemName;
        private int amount;
        private int finalPrice;
        private int discountAmount;
        private OrderStatus status;
        private int saveReward;

        private OrderCancel orderCancel;
        private OrderReturn orderReturn;
        private OrderExchange orderExchange;
    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SelectOptionDto{
        private String optionName;
        private int optionAmount;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class OrderDto{

        private Long orderId;
        private String merchantUid;
        private LocalDateTime paymentDate; // 결제 시간
        private boolean isPackage; // 묶음배송여부

        private String deliveryNumber;
        private LocalDateTime arrivalDate; // 도착일

        private int orderPrice;
        private int deliveryPrice;
        private int discountTotal;
        private int discountReward;
        private int discountCoupon;
        private int paymentPrice;
        private PaymentMethod paymentMethod;

        private String name;
        private String phone;
        private String zipcode;
        private String street;
        private String detailAddress;
        private String request; // 요청사항


    }


}
