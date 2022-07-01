package com.bi.barfdog.api.orderDto;

import com.bi.barfdog.domain.order.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryAdminOrdersDto {

    private Long id;
    private String merchantUid;
    private OrderStatus orderStatus;
    private LocalDateTime OrderDate;
    private String memberEmail;
    private String memberName;
    private String memberPhoneNumber;
    private String recipientName;
    private String recipientPhoneNumber;
    private boolean isPackageDelivery;
}
