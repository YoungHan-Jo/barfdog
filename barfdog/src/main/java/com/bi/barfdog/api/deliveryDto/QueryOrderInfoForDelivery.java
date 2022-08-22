package com.bi.barfdog.api.deliveryDto;

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
public class QueryOrderInfoForDelivery {

    private String transUniqueCd;

    private String sndName;
    private String sndZipCode;
    private String sndAddr1;
    private String sndAddr2;
    private String sndTel1;

    private String rcvName;
    private String rcvZipCode;
    private String rcvAddr1;
    private String rcvAddr2;
    private String rcvTel1;

    private String mallId;

    private String request; // 요청사항

    @Builder.Default
    private List<OrderItemDto> orderItems = new ArrayList<>();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class OrderItemDto {

        private String uniqueCd;
        private String ordNo;
        private String itemName;
        private int itemQty;
        private String ordDate;

    }






}

