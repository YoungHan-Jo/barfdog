package com.bi.barfdog.domain.dog;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Embeddable
public class DogAnalysis {

    private BigDecimal oneDayRecommendKcal;
    private BigDecimal oneDayRecommendGram;
    private BigDecimal oneMealRecommendGram;

}
