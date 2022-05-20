package com.bi.barfdog.repository;

import com.bi.barfdog.domain.recipe.QRecipe;
import com.bi.barfdog.domain.subscribeRecipe.QSubscribeRecipe;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.bi.barfdog.domain.recipe.QRecipe.*;
import static com.bi.barfdog.domain.subscribeRecipe.QSubscribeRecipe.*;

@RequiredArgsConstructor
@Repository
public class SubscribeRecipeRepositoryImpl implements SubscribeRecipeRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<String> findRecipeNamesBySubscribeId(Long id) {
        return queryFactory
                .select(recipe.name)
                .from(subscribeRecipe)
                .join(subscribeRecipe.recipe, recipe)
                .where(subscribeRecipe.subscribe.id.eq(id))
                .fetch();
    }

}
