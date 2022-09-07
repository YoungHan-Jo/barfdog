package com.bi.barfdog.repository.recipe;

import com.bi.barfdog.api.recipeDto.RecipeSurveyResponseDto;
import com.bi.barfdog.api.reviewDto.ReviewRecipesDto;
import com.bi.barfdog.domain.recipe.Recipe;
import com.bi.barfdog.domain.recipe.RecipeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RecipeRepository extends JpaRepository<Recipe, Long>,RecipeRepositoryCustom{

    Optional<Recipe> findByName(String name);

    List<Recipe> findByStatus(RecipeStatus active);

}
