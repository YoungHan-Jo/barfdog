package com.bi.barfdog.api.recipeDto;

import com.bi.barfdog.api.InfoController;
import com.bi.barfdog.domain.recipe.Leaked;
import com.bi.barfdog.domain.recipe.Recipe;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

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

    private String filename1;
    private String thumbnailUri1;
    private String filename2;
    private String thumbnailUri2;

    private Leaked leaked;
    private boolean inStock;

    public void setThumbnailUriAndFilename(Recipe recipe) {
        String filename1 = recipe.getThumbnailImage().getFilename1();
        String filename2 = recipe.getThumbnailImage().getFilename2();

        this.filename1 = filename1;
        this.filename2 = filename2;
        thumbnailUri1 = linkTo(InfoController.class).slash("display/recipes?filename=" + filename1).toString();
        thumbnailUri2 = linkTo(InfoController.class).slash("display/recipes?filename=" + filename2).toString();
    }
}