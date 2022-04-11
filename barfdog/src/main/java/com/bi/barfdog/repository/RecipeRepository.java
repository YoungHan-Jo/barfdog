package com.bi.barfdog.repository;

import com.bi.barfdog.api.recipeDto.RecipeSurveyResponseDto;
import com.bi.barfdog.domain.recipe.Recipe;
import com.bi.barfdog.domain.recipe.RecipeStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecipeRepository extends JpaRepository<Recipe, Long>{

    Optional<Recipe> findByName(String name);

    List<Recipe> findByStatus(RecipeStatus active);
}
