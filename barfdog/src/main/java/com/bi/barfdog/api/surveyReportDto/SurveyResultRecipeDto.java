package com.bi.barfdog.api.surveyReportDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SurveyResultRecipeDto {

    private Long id;

    private String name;

    private String description;

    private BigDecimal pricePerGram;

    private BigDecimal gramPerKcal;

    private boolean inStock;

    private String imgUrl;

}
