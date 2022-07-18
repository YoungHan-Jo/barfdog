package com.bi.barfdog.api.barfDto;

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
public class AdminDashBoardResponseDto {

    private Long newOrderCount;
    private Long newMemberCount;

    private Long subscribePendingCount;

    @Builder.Default
    private List<OrderStatusCountDto> orderStatusCountDtoList = new ArrayList<>();

    @Builder.Default
    private List<OrderCountByMonth> orderCountByMonthList = new ArrayList<>();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderStatusCountDto {
        private OrderStatus orderstatus;
        private Long count;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderCountByMonth {

        private String month;
        private Long generalCount;
        private Long subscribeCount;

    }


}
