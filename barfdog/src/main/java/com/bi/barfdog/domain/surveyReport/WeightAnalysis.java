package com.bi.barfdog.domain.surveyReport;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@AllArgsConstructor
@NoArgsConstructor
@Getter  @Builder
@Embeddable
public class WeightAnalysis {

    private double avgWeight;
    private int weightGroupOneCount;
    private int weightGroupTwoCount;
    private int weightGroupThreeCount;
    private int weightGroupFourCount;
    private int weightGroupFiveCount;
    private String myWeightGroup;

}
