package com.bi.barfdog.goodsFlow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckTraceResultRequestDto {

    private String uniqueCd;
    private String seq;

}
