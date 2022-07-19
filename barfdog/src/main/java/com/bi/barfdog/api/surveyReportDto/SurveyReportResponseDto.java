package com.bi.barfdog.api.surveyReportDto;

import com.bi.barfdog.domain.dog.DogActivity;
import com.bi.barfdog.domain.dog.DogSize;
import com.bi.barfdog.domain.surveyReport.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SurveyReportResponseDto {

    private LocalDate lastSurveyDate;
    private String myDogName;
    private DogSize dogSize; // [LARGE, MIDDLE, SMALL]
    private DogActivity dogActivity; // 활동량 관련

    private AgeAnalysis ageAnalysis;

    private WeightAnalysis weightAnalysis;

    private ActivityAnalysis activityAnalysis;

    private WalkingAnalysis walkingAnalysis;

    private SnackAnalysis snackAnalysis;

    private FoodAnalysis foodAnalysis;

}
