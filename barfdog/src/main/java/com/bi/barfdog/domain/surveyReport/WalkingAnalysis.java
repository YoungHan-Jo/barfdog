package com.bi.barfdog.domain.surveyReport;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Builder
@Embeddable
public class WalkingAnalysis {

    private double highRankPercent;
    private double totalWalingTime;

    private double avgWalkingTimeInCity;
    private double avgWalkingTimeInAge;
    private double aveWalkingTimeInDogSize;

}
