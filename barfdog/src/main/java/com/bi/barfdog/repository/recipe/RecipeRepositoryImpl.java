package com.bi.barfdog.repository.recipe;

import com.bi.barfdog.api.barfDto.HomePageDto;
import com.bi.barfdog.api.reviewDto.ReviewRecipesDto;
import com.bi.barfdog.domain.recipe.Leaked;
import com.bi.barfdog.domain.recipe.QRecipe;
import com.bi.barfdog.domain.recipe.RecipeStatus;
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

    @Override
    public List<HomePageDto.RecipeDto> findRecipeDto() {
        List<HomePageDto.RecipeDto> result = queryFactory
                .select(Projections.constructor(HomePageDto.RecipeDto.class,
                        recipe.id,
                        recipe.name,
                        recipe.description,
                        recipe.uiNameKorean,
                        recipe.uiNameEnglish,
                        recipe.thumbnailImage.filename1,
                        recipe.thumbnailImage.filename1,
                        recipe.thumbnailImage.filename2,
                        recipe.thumbnailImage.filename2
                ))
                .from(recipe)
                .where(recipe.status.eq(RecipeStatus.ACTIVE).and(recipe.leaked.eq(Leaked.LEAKED)))
                .orderBy(recipe.createdDate.desc())
                .fetch();
        for (HomePageDto.RecipeDto dto : result) {
            dto.changeUrl();
        }
        return result;
    }
}
