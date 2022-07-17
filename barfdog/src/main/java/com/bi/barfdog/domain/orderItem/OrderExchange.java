package com.bi.barfdog.domain.orderItem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.time.LocalDateTime;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class OrderExchange {
    private String exchangeReason;
    private String exchangeDetailReason;
    private LocalDateTime exchangeRequestDate;
    private LocalDateTime exchangeConfirmDate;
}
