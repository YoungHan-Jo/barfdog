package com.bi.barfdog.domain.dog;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class DogActivity {

    private int activityLevel; // 활동량 1~5
    private int walkingCountPerWeek; // 산책 회수
    private double walkingTimePerOneTime; // 산책 시간

}


