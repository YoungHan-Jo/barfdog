package com.bi.barfdog.repository.recipe;

import com.bi.barfdog.api.barfDto.HomePageDto;
import com.bi.barfdog.api.reviewDto.ReviewRecipesDto;

import java.util.List;

public interface RecipeRepositoryCustom {
    List<ReviewRecipesDto> findReviewRecipesDto();

    List<HomePageDto.RecipeDto> findRecipeDto();
}
