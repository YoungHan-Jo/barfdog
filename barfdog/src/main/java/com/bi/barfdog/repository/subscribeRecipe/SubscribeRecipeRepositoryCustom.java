package com.bi.barfdog.repository.subscribeRecipe;

import java.util.List;

public interface SubscribeRecipeRepositoryCustom {
    List<String> findRecipeNamesBySubscribeId(Long id);
}
