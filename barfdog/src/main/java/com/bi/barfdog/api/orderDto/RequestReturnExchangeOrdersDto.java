package com.bi.barfdog.api.orderDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestReturnExchangeOrdersDto {

    @Builder.Default
    private List<Long> orderItemIdList = new ArrayList<>();
    @NotEmpty
    private String reason;

    private String detailReason;

}
