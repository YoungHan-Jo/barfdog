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

    private int subscribeCount;

    private SubscribePlan plan;

    private BigDecimal amount;

    private int paymentPrice;

    private LocalDate deliveryDate;

    private String inedibleFood;
    private String inedibleFoodEtc; // 못먹는 음식 기타 일 때
    private String caution; // 질병 및 주의사항
}
