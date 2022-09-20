package com.bi.barfdog.api.dogDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateDogResponseDto {

    private BigDecimal oneMealRecommendGram;
}
