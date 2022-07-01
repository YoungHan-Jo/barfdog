package com.bi.barfdog.api.orderDto;

import com.bi.barfdog.domain.Address;
import com.bi.barfdog.domain.subscribe.SubscribePlan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderSheetSubscribeResponseDto {

    private SubscribeDto subscribeDto;

    private List<String> recipeNameList;

    private String name;
    private String email;
    private String phoneNumber;

    private Address address;

    @Builder.Default
    private List<OrderSheetSubsCouponDto> coupons = new ArrayList();

    private int reward;

    private boolean brochure;

    @Data
    @AllArgsConstructor
    public static class SubscribeDto {

        private Long id;
        private SubscribePlan plan;
        private int nextPaymentPrice;

    }

}
