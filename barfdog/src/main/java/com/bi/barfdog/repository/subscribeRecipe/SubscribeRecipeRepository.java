package com.bi.barfdog.repository.subscribeRecipe;

import com.bi.barfdog.domain.subscribe.Subscribe;
import com.bi.barfdog.domain.subscribeRecipe.SubscribeRecipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscribeRecipeRepository extends JpaRepository<SubscribeRecipe, Long>, SubscribeRecipeRepositoryCustom {
    void deleteAllBySubscribe(Subscribe subscribe);

    List<SubscribeRecipe> findBySubscribe(Subscribe findSubscribe);
}
