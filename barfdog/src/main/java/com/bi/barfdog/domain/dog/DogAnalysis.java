package com.bi.barfdog.domain.dog;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Embeddable
public class DogAnalysis {

    private int oneDayRecommendKcal;
    private int oneDayRecommendGram;
    private int oneMealRecommendGram;

}
