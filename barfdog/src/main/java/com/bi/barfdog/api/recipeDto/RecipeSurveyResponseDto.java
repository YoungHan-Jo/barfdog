package com.bi.barfdog.api.recipeDto;

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
public class RecipeSurveyResponseDto {

    private Long id;
    private String descriptionForSurvey;
    private List<String> ingredients = new ArrayList<>();

}
