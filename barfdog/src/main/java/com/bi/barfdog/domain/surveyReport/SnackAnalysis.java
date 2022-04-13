package com.bi.barfdog.domain.surveyReport;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Embeddable
public class SnackAnalysis {

    private double avgSnackCountInLargeDog;
    private double avgSnackCountInMiddleDog;
    private double avgSnackCountInSmallDog;
    private int mySnackCount;

}
