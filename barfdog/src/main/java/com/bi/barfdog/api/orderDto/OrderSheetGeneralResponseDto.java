package com.bi.barfdog.api.orderDto;

import com.bi.barfdog.domain.Address;
import com.bi.barfdog.domain.member.Grade;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderSheetGeneralResponseDto {

    @Builder.Default
    private List<OrderItemDto> orderItemDtoList = new ArrayList<>();


    private String name;
    private String email;
    private String phoneNumber;
    private Address address;

    private Long deliveryId;
    private LocalDate nextSubscribeDeliveryDate; // 있으면 날짜 / 없으면 null

    @Builder.Default
    private List<OrderSheetGeneralCouponDto> coupons = new ArrayList();

    private int orderPrice;
    private int reward;

    private int deliveryPrice; // 기본 배송비
    private int freeCondition; // xx 원 이상 무료 배송 조건

    private boolean brochure;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class OrderItemDto{

        private Long itemId;
        private String name;

        @Builder.Default
        private List<OptionDto> optionDtoList = new ArrayList<>();

        private int amount;

        private int originalOrderLinePrice;
        private int orderLinePrice;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class OptionDto {
        private Long optionId;
        private String name;
        private int price;
        private int amount;
    }




}
