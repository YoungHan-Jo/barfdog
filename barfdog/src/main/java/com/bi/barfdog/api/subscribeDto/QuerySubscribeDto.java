package com.bi.barfdog.api.subscribeDto;

import com.bi.barfdog.api.InfoController;
import com.bi.barfdog.domain.coupon.DiscountType;
import com.bi.barfdog.domain.subscribe.SubscribePlan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuerySubscribeDto {

    private SubscribeDto subscribeDto;
    @Builder.Default
    private List<SubscribeRecipeDto> subscribeRecipeDtoList = new ArrayList<>();
    @Builder.Default
    private List<MemberCouponDto> memberCouponDtoList = new ArrayList<>();
    @Builder.Default
    private List<RecipeDto> recipeDtoList = new ArrayList<>();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SubscribeDto{
        private Long id;
        private String dogName;
        private int subscribeCount;
        private SubscribePlan plan; // [FULL, HALF, TOPPING]
        private BigDecimal oneMealRecommendGram;
        private LocalDateTime nextPaymentDate;
        private int nextPaymentPrice;
        private LocalDate nextDeliveryDate;
        private Long usingMemberCouponId;
        private String couponName;
        private int discount;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SubscribeRecipeDto {
        private Long recipeId;
        private String recipeName;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MemberCouponDto {

        private Long memberCouponId;
        private String name;
        private DiscountType discountType; // 할인 타입 [ FIXED_RATE, FLAT_RATE ]
        private int discountDegree; // 할인 정도 ( 원/% )
        private int availableMaxDiscount; // 적용가능 최대 할인금액
        private int availableMinPrice; // 사용가능한 최소 금액
        private int remaining; // 남은 개수
        private LocalDateTime expiredDate; // 쿠폰 유효 기간
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RecipeDto{
        private Long id;

        private String name;

        private String description;

        private BigDecimal pricePerGram;

        private BigDecimal gramPerKcal;

        private boolean inStock;

        private String imgUrl;

        public void changeUrl(String filename) {
            imgUrl = linkTo(InfoController.class).slash("display/recipes?filename=" + filename).toString();
        }
    }


}
