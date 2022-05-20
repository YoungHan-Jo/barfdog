package com.bi.barfdog.api.memberDto;

import com.bi.barfdog.domain.subscribe.SubscribePlan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuerySubscribeAdminDto {

    private Long id;

    private String dogName;

    private LocalDateTime subscribeStartDate;

    private SubscribePlan plan;

    private BigDecimal amount;

    private int paymentPrice;

    private LocalDate deliveryDate;
}
