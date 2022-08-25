package com.bi.barfdog.api.orderDto;

import com.bi.barfdog.domain.order.OrderStatus;
import com.bi.barfdog.domain.order.PaymentMethod;
import com.bi.barfdog.domain.subscribe.SubscribePlan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryAdminSubscribeOrderDto {

    private SubscribeOrderInfoDto subscribeOrderInfoDto;
    private DogDto dogDto;
    private SubscribeDto subscribeDto;
    private SubscribeDto beforeSubscribeDto;
    private SubscribePaymentDto subscribePaymentDto;
    private SubscribeDeliveryDto subscribeDeliveryDto;

    @Data
    @AllArgsConstructor
    public static class SubscribeOrderInfoDto {

        private Long id;
        private String merchantUid;
        private LocalDateTime orderDate;
        private String orderType;
        private boolean isPackage;

        private String memberName;
        private String phoneNumber;
        private String Email;
        private boolean isSubscribe;

        private String cancelReason;
        private String cancelDetailReason;
        private LocalDateTime cancelRequestDate;
        private LocalDateTime cancelConfirmDate;

    }

    @Data
    @AllArgsConstructor
    public static class DogDto {

        private String name;
        private String inedibleFood;
        private String inedibleFoodEtc; // 못먹는 음식 기타 일 때
        private String caution; // 질병 및 주의사항

    }

    @Data
    @AllArgsConstructor
    public static class SubscribeDto {
        private Long id;
        private int subscribeCount;
        private SubscribePlan plan; // [FULL, HALF, TOPPING]
        private BigDecimal oneMealRecommendGram;
        private String recipeName; // [ , 로 구분]

        public void changeRecipeName(List<String> recipeNames) {
            recipeName = recipeNames.get(0);
            if (recipeNames.size() > 1) {
                for (int i = 1; i < recipeNames.size(); ++i) {
                    recipeName += "," + recipeNames.get(i);
                }
            }
        }
    }



    @Data
    @AllArgsConstructor
    public static class SubscribePaymentDto {

        private int orderPrice; // 주문 가격
        private int deliveryPrice; // 배송비
        private int discountReward; // 사용한 적립금
        private String couponName;
        private int discountCoupon; // 쿠폰 적용 할인 금액
        private int paymentPrice; // 결제 금액
        private PaymentMethod paymentMethod;
        private OrderStatus orderStatus;

        private LocalDateTime orderConfirmDate;

    }

    @Data
    @AllArgsConstructor
    public static class SubscribeDeliveryDto {

        private String recipientName;
        private String recipientPhone;
        private String zipcode;
        private String street;
        private String detailAddress;

        private LocalDateTime departureDate; // 출발일
        private LocalDateTime arrivalDate; // 도착일

        private String deliveryNumber;
        private String request;

    }
}
