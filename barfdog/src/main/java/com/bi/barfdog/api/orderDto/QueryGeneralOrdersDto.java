package com.bi.barfdog.api.orderDto;

import com.bi.barfdog.domain.order.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryGeneralOrdersDto {

    private String thumbnailUrl;

    private OrderDto orderDto;

    @Builder.Default
    private List<String> itemNameList = new ArrayList<>();


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderDto {

        private Long id;
        private String merchantUid;
        private int paymentPrice;
        private OrderStatus orderStatus;

    }
}

