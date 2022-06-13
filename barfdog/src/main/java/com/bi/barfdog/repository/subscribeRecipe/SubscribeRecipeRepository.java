package com.bi.barfdog.repository.subscribeRecipe;

import com.bi.barfdog.domain.subscribeRecipe.SubscribeRecipe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscribeRecipeRepository extends JpaRepository<SubscribeRecipe, Long>, SubscribeRecipeRepositoryCustom {
}
