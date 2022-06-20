package com.bi.barfdog.repository.recipe;

import com.bi.barfdog.api.reviewDto.ReviewRecipesDto;
import com.bi.barfdog.domain.recipe.QRecipe;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.bi.barfdog.domain.recipe.QRecipe.*;

@RequiredArgsConstructor
@Repository
public class RecipeRepositoryImpl implements RecipeRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ReviewRecipesDto> findReviewRecipesDto() {
        return queryFactory
                .select(Projections.constructor(ReviewRecipesDto.class,
                        recipe.id,
                        recipe.name
                        ))
                .from(recipe)
                .orderBy(recipe.createdDate.asc())
                .fetch()
                ;
    }
}
