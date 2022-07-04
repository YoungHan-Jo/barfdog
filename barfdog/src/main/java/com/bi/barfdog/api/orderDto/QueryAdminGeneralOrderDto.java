package com.bi.barfdog.api.orderDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryAdminGeneralOrderDto {

    private Long id;
    private String merchantUid;
    private LocalDateTime orderDate;
    private String orderType;
    private boolean isPackage;

    private String memberName;
    private String phoneNumber;
    private String Email;
    private boolean isSubscribe;


    @Data
    @AllArgsConstructor
    public static class OrderItemDto {

        private Long id;
        private String itemName;
        private

    }
}
