package com.bi.barfdog.api.deliveryDto;

import com.bi.barfdog.api.OrderApiController;
import com.bi.barfdog.domain.delivery.DeliveryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryGeneralDeliveriesDto {

    private OrderDeliveryDto orderDeliveryDto;

    @Builder.Default
    private List<String> itemNameList = new ArrayList<>();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderDeliveryDto{
        private Long orderId;
        private String orderInfoUrl;
        private LocalDateTime orderDate;
        private DeliveryStatus deliveryStatus;
        private String deliveryNumber;

        public void changeUrl(Long orderId) {
            orderInfoUrl = linkTo(OrderApiController.class).slash(orderId).slash("general").toString();
        }
    }



}
