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

    private ActivityLevel activityLevel; // [VERY_LITTLE, LITTLE, NORMAL, MUCH, VERY_MUCH]
    private int walkingCountPerWeek; // 산책 회수
    private double walkingTimePerOneTime; // 산책 시간

}


