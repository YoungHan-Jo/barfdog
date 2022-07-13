package com.bi.barfdog.api.orderDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SuccessGeneralRequestDto {

    private String impUid; // 아임포트 결제번호
    private String merchantUid; // 주문번호 yymmdd + 당일주문 순서(001~999)

}
