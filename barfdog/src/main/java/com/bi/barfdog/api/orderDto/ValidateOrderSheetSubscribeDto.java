package com.bi.barfdog.api.orderDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ValidateOrderSheetSubscribeDto {

    private Long recipeId; // 레시피 품절 체크

    private BigDecimal recipePrice; // 레시피 가격

    private Long couponId; // 사용한 쿠폰




}
