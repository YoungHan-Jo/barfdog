package com.bi.barfdog.api.recipeDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurveyResponseDto {

    List<String> ingredients = new ArrayList<>();
    List<RecipeSurveyResponseDto> recipeSurveyResponseDtos = new ArrayList<>();

}
