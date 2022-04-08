package com.bi.barfdog.api.recipeDto;

import com.bi.barfdog.domain.recipe.Leaked;
import com.bi.barfdog.domain.recipe.Recipe;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecipeResponseDto {

    private Long id;
    private String name;
    private String description;
    private String uiNameKorean;
    private String uiNameEnglish;
    private BigDecimal pricePerGram;
    private BigDecimal gramPerKcal;
    private List<String> ingredientList;
    private String descriptionForSurvey;

    private String thumbnailUri1;
    private String thumbnailUri2;

    private Leaked leaked;
    private boolean inStock;

    public void setThumbnailUri(Recipe recipe) {
        String rootFolder = recipe.getThumbnailImage().getFolder();
        String filename1 = recipe.getThumbnailImage().getFilename1();
        String filename2 = recipe.getThumbnailImage().getFilename2();

        thumbnailUri1 = rootFolder + "/" + filename1;
        thumbnailUri2 = rootFolder + "/" + filename2;

    }
}