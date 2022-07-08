package com.bi.barfdog.api.deliveryDto;

import com.bi.barfdog.api.OrderApiController;
import com.bi.barfdog.domain.delivery.DeliveryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryDeliveriesDto {

    private String recipeName;
    private DeliveryDto deliveryDto;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DeliveryDto {

        private Long orderId;
        private String orderInfoUrl;
        private LocalDateTime orderDate;
        private int subscribeCount;
        private String dogName;
        private LocalDate produceDate;
        private LocalDate nextDeliveryDate;
        private DeliveryStatus deliveryStatus;
        private String deliveryNumber;

        public void changeUrlAndDate(Long orderId, LocalDate nextDeliveryDate) {
            orderInfoUrl = linkTo(OrderApiController.class).slash(orderId).slash("subscribe").toString();
            produceDate = nextDeliveryDate.minusDays(4);
        }
    }

}
