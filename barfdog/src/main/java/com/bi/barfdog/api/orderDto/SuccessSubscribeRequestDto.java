package com.bi.barfdog.api.orderDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SuccessSubscribeRequestDto {

    @NotEmpty
    private String impUid; // 아임포트 결제번호
    @NotEmpty
    private String merchantUid; // 주문번호

    @NotEmpty
    private String customerUid; // 카드결제 uid

}
