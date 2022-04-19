package com.bi.barfdog.api.surveyReportDto;

import com.bi.barfdog.domain.recipe.Recipe;
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

    private String dogName;

    private Long recommendRecipeId;
    private String recommendRecipeName;
    private String recommendRecipeDescription;

    private String recommendRecipeImgUrl;
    private String uiNameKorean;
    private String uiNameEnglish;

    private FoodAnalysis foodAnalysis;

    private List<SurveyResultRecipeDto> recipeDtoList = new ArrayList<>();
}
