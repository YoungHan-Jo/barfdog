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
@Getter @Builder
public class OrderReturn {

    private String returnReason;
    private String returnDetailReason;
    private LocalDateTime returnRequestDate;
    private LocalDateTime returnConfirmDate;

}
