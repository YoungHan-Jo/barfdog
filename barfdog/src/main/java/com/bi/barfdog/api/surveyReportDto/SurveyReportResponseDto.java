package com.bi.barfdog.api.surveyReportDto;

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

    private AgeAnalysis ageAnalysis;

    private WeightAnalysis weightAnalysis;

    private ActivityAnalysis activityAnalysis;

    private WalkingAnalysis walkingAnalysis;

    private SnackAnalysis snackAnalysis;

    private FoodAnalysis foodAnalysis;

}
