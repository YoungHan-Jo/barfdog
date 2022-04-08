package com.bi.barfdog.api.recipeDto;

import com.bi.barfdog.domain.recipe.Leaked;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecipeRequestDto {

    @NotEmpty
    private String name;
    @NotEmpty
    private String description;
    @NotEmpty
    private String uiNameKorean;
    @NotEmpty
    private String uiNameEnglish;
    @NotEmpty
    private String pricePerGram;
    @NotEmpty
    private String gramPerKcal;
    @NotEmpty
    private String ingredients;
    @NotEmpty
    private String descriptionForSurvey;

    private Leaked leaked;
    private boolean inStock;
}
