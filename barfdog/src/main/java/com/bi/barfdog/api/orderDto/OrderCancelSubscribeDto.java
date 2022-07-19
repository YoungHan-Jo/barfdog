package com.bi.barfdog.api.orderDto;

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
public class OrderCancelSubscribeDto {

    @Builder.Default
    private List<Long> orderIdList = new ArrayList<>();

    private String reason;
    private String detailReason;

}
