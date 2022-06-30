package com.bi.barfdog.service;

import com.bi.barfdog.api.subscribeDto.UpdateSubscribeDto;
import com.bi.barfdog.domain.recipe.Recipe;
import com.bi.barfdog.domain.subscribe.Subscribe;
import com.bi.barfdog.domain.subscribeRecipe.SubscribeRecipe;
import com.bi.barfdog.repository.recipe.RecipeRepository;
import com.bi.barfdog.repository.subscribe.SubscribeRepository;
import com.bi.barfdog.repository.subscribeRecipe.SubscribeRecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class SubscribeService {

    private final SubscribeRepository subscribeRepository;
    private final RecipeRepository recipeRepository;
    private final SubscribeRecipeRepository subscribeRecipeRepository;

    @Transactional
    public void updateSubscribe(Long id, UpdateSubscribeDto requestDto) {
        Subscribe subscribe = subscribeRepository.findById(id).get();
        subscribe.update(requestDto);
        subscribeRecipeRepository.deleteAllBySubscribe(subscribe);
        saveSubscribeRecipes(requestDto, subscribe);
    }

    private void saveSubscribeRecipes(UpdateSubscribeDto requestDto, Subscribe subscribe) {
        List<Recipe> recipeList = recipeRepository.findAllById(requestDto.getRecipeIdList());
        for (Recipe recipe : recipeList) {
            SubscribeRecipe subscribeRecipe = SubscribeRecipe.builder()
                    .subscribe(subscribe)
                    .recipe(recipe)
                    .build();
            subscribeRecipeRepository.save(subscribeRecipe);
        }
    }
}
