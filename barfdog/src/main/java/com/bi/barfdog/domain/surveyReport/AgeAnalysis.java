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
public class AgeAnalysis {

    private int avgAgeMonth;
    private int ageGroupOneCount;
    private int ageGroupTwoCount;
    private int ageGroupThreeCount;
    private int ageGroupFourCount;
    private int ageGroupFiveCount;
    private int myAgeGroup;

}
