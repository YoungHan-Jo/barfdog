package com.bi.barfdog.service;

import com.bi.barfdog.api.subscribeDto.UpdateSubscribeDto;
import com.bi.barfdog.domain.recipe.Recipe;
import com.bi.barfdog.domain.subscribe.BeforeSubscribe;
import com.bi.barfdog.domain.subscribe.Subscribe;
import com.bi.barfdog.domain.subscribeRecipe.SubscribeRecipe;
import com.bi.barfdog.repository.order.OrderRepository;
import com.bi.barfdog.repository.recipe.RecipeRepository;
import com.bi.barfdog.repository.subscribe.BeforeSubscribeRepository;
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
    private final BeforeSubscribeRepository beforeSubscribeRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public void updateSubscribe(Long id, UpdateSubscribeDto requestDto) {
        Subscribe subscribe = subscribeRepository.findById(id).get();

        String recipeName = getRecipeName(subscribe);

        BeforeSubscribe newBeforeSubscribe = saveNewBeforeSubscribe(subscribe, recipeName);
        subscribe.setBeforeSubscribe(newBeforeSubscribe);

//        BeforeSubscribe findBeforeSubscribe = subscribe.getBeforeSubscribe();
//        if (findBeforeSubscribe == null) {
//            BeforeSubscribe newBeforeSubscribe = BeforeSubscribe.builder()
//                    .build();
//            beforeSubscribeRepository.save(newBeforeSubscribe);
//            subscribe.setBeforeSubscribe(newb);
//        } else {
//            findBeforeSubscribe.change();
//
//        }

        subscribe.update(requestDto);
        subscribeRecipeRepository.deleteAllBySubscribe(subscribe);
        saveSubscribeRecipes(requestDto, subscribe);
    }

    private BeforeSubscribe saveNewBeforeSubscribe(Subscribe subscribe, String recipeName) {
        BeforeSubscribe newBeforeSubscribe = BeforeSubscribe.builder()
                .subscribeCount(subscribe.getSubscribeCount())
                .plan(subscribe.getPlan())
                .oneMealRecommendGram(subscribe.getDog().getSurveyReport().getFoodAnalysis().getOneMealRecommendGram())
                .recipeName(recipeName)
                .paymentPrice(subscribe.getNextPaymentPrice())
                .build();
        beforeSubscribeRepository.save(newBeforeSubscribe);
        return newBeforeSubscribe;
    }

    private String getRecipeName(Subscribe subscribe) {
        List<SubscribeRecipe> subscribeRecipes = subscribeRecipeRepository.findBySubscribe(subscribe);
        String recipeName = subscribeRecipes.get(0).getRecipe().getName();
        if (subscribeRecipes.size() > 1) {
            recipeName += "," +subscribeRecipes.get(1).getRecipe().getName();
        }
        return recipeName;
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
