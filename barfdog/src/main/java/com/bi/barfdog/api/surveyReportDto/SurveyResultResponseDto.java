package com.bi.barfdog.api.surveyReportDto;

import com.bi.barfdog.domain.subscribe.SubscribeStatus;
import com.bi.barfdog.domain.surveyReport.FoodAnalysis;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SurveyResultResponseDto {

    private Long dogId;
    private String dogName;
    private Long subscribeId;
    private SubscribeStatus subscribeStatus;

    private Long recommendRecipeId;
    private String recommendRecipeName;
    private String recommendRecipeDescription;

    private String recommendRecipeImgUrl;
    private String uiNameKorean;
    private String uiNameEnglish;

    private FoodAnalysis foodAnalysis;

    @Builder.Default
    private List<SurveyResultRecipeDto> recipeDtoList = new ArrayList<>();

}
