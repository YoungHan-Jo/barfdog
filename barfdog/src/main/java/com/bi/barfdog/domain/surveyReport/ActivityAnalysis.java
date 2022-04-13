package com.bi.barfdog.domain.surveyReport;

import com.bi.barfdog.domain.dog.ActivityLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Builder
@Embeddable
public class ActivityAnalysis {

    private ActivityLevel avgActivityLevel;
    private int activityGroupOneCount;
    private int activityGroupTwoCount;
    private int activityGroupThreeCount;
    private int activityGroupFourCount;
    private int activityGroupFiveCount;
    private int myActivityGroup;

}
