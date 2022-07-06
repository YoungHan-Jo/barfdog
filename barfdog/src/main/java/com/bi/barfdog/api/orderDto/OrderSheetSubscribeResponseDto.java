package com.bi.barfdog.api.orderDto;

import com.bi.barfdog.domain.Address;
import com.bi.barfdog.domain.member.Grade;
import com.bi.barfdog.domain.subscribe.SubscribePlan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
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
    private Grade grade;
    private int gradeDiscountPercent;
    private String email;
    private String phoneNumber;


    private Address address;

    private LocalDate nextDeliveryDate;

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
