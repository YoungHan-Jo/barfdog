package com.bi.barfdog.api.orderDto;

import com.bi.barfdog.api.InfoController;
import com.bi.barfdog.domain.delivery.DeliveryStatus;
import com.bi.barfdog.domain.order.OrderStatus;
import com.bi.barfdog.domain.order.PaymentMethod;
import com.bi.barfdog.domain.subscribe.SubscribePlan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuerySubscribeOrderDto {

    private RecipeDto recipeDto;
    private String recipeNames;
    private OrderDto orderDto;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RecipeDto{
        private String thumbnailUrl;
        private String recipeName;

        public void changUrl(String filename) {
            thumbnailUrl = linkTo(InfoController.class).slash("display/recipes?filename=" + filename).toString();
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderDto {
        private int subscribeCount;
        private String dogName;
        private BigDecimal oneMealRecommendGram;
        private SubscribePlan plan; // [FULL, HALF, TOPPING]
        private int orderPrice;

        private int beforeSubscribeCount;
        private SubscribePlan beforePlan; // [FULL, HALF, TOPPING]
        private BigDecimal beforeOneMealRecommendGram;
        private String beforeRecipeName; // [ , 로 구분]
        private int beforeOrderPrice; // 주문 가격

        private OrderStatus orderStatus;
        private LocalDateTime cancelRequestDate;
        private LocalDateTime cancelConfirmDate;
        private String cancelReason;
        private String cancelDetailReason;

        private String merchantUid;
        private String orderType;
        private LocalDateTime orderDate;

        private String deliveryNumber;
        private DeliveryStatus deliveryStatus;

        private int deliveryPrice;
        private int discountTotal;
        private int discountReward;
        private int discountCoupon;
        private int discountGrade;

        private int paymentPrice;
        private PaymentMethod paymentMethod; // [CREDIT_CARD, NAVER_PAY, KAKAO_PAY]

        private String recipientName;
        private String recipientPhone;
        private String zipcode;
        private String street;
        private String detailAddress;
        private String request; // 요청사항

    }

//    @Data
//    @AllArgsConstructor
//    @NoArgsConstructor
//    public static class BeforeSubscribeDto{
//
//        private int beforeSubscribeCount;
//        private SubscribePlan beforePlan; // [FULL, HALF, TOPPING]
//        private BigDecimal beforeOneMealRecommendGram;
//        private String beforeRecipeName; // [ , 로 구분]
//        private int beforeOrderPrice; // 주문 가격
//    }
//
//    @Data
//    @AllArgsConstructor
//    @NoArgsConstructor
//    public static class DeliveryDto{
//        private String deliveryNumber;
//        private DeliveryStatus status;
//
//        private String name;
//        private String phone;
//
//        private String zipcode;
//        private String street;
//        private String detailAddress;
//
//        private String request; // 요청사항
//    }

}
