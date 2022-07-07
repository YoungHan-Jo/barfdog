package com.bi.barfdog.api.orderDto;

import com.bi.barfdog.api.InfoController;
import com.bi.barfdog.domain.order.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuerySubscribeOrdersDto {

    private RecipeDto recipeDto;
    private SubscribeOrderDto subscribeOrderDto;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RecipeDto {
        private String thumbnailUrl;
        private String recipeName;

        public void changeUrl(String filename) {
            thumbnailUrl = linkTo(InfoController.class).slash("display/recipes?filename = " + filename).toString();
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SubscribeOrderDto {
        private Long orderId;
        private Long subscribeId;
        private LocalDateTime orderDate;
        private String dogName;
        private int subscribeCount;
        private String merchantUid;
        private int paymentPrice;
        private OrderStatus orderStatus;
    }


}
