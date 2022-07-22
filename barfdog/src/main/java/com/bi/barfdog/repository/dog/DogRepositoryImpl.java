package com.bi.barfdog.repository.dog;

import com.bi.barfdog.api.dogDto.QueryDogDto;
import com.bi.barfdog.api.dogDto.QueryDogsDto;
import com.bi.barfdog.api.recipeDto.RecipeSurveyResponseDto;
import com.bi.barfdog.domain.dog.*;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.subscribe.QSubscribe;
import com.bi.barfdog.repository.recipe.RecipeRepository;
import com.bi.barfdog.service.RecipeService;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.bi.barfdog.domain.dog.QDog.dog;
import static com.bi.barfdog.domain.dog.QDogPicture.*;
import static com.bi.barfdog.domain.member.QMember.*;
import static com.bi.barfdog.domain.subscribe.QSubscribe.*;


@RequiredArgsConstructor
@Repository
public class DogRepositoryImpl implements DogRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    private final RecipeService recipeService;


    @Override
    public double findAvgStartAgeMonth() {
        Double result = queryFactory
                .select(dog.startAgeMonth.avg())
                .from(dog)
                .fetchOne();
        return result;
    }

    @Override
    public List<String> findAgeGroup(long monthRange) {
        List<String> results = queryFactory
                .select(new CaseBuilder()
                        .when(dog.startAgeMonth.between(0, monthRange)).then("1")
                        .when(dog.startAgeMonth.between(monthRange + 1, monthRange * 2)).then("2")
                        .when(dog.startAgeMonth.between(monthRange * 2 + 1, monthRange * 3)).then("3")
                        .when(dog.startAgeMonth.between(monthRange * 3 + 1, monthRange * 4)).then("4")
                        .otherwise("5"))
                .from(dog)
                .fetch();
        return results;
    }

    @Override
    public List<String> findWeightGroupByDogSize(DogSize dogSize, double lightestWeight, double weightRange) {
        List<String> results = queryFactory
                .select(new CaseBuilder()
                        .when(dog.weight.between(lightestWeight, lightestWeight + weightRange)).then("1")
                        .when(dog.weight.between(lightestWeight + weightRange + 0.01, lightestWeight + weightRange * 2)).then("2")
                        .when(dog.weight.between(lightestWeight + weightRange * 2 + 0.01, lightestWeight + weightRange * 3)).then("3")
                        .when(dog.weight.between(lightestWeight + weightRange * 2 + 0.01, lightestWeight + weightRange * 4)).then("4")
                        .otherwise("5"))
                .from(dog)
                .where(dog.dogSize.eq(dogSize))
                .fetch();
        return results;
    }

    @Override
    public List<String> findActivityGroupByDogSize(DogSize dogSize) {
        List<String> results = queryFactory
                .select(new CaseBuilder()
                        .when(dog.dogActivity.activityLevel.eq(ActivityLevel.VERY_LITTLE)).then("1")
                        .when(dog.dogActivity.activityLevel.eq(ActivityLevel.LITTLE)).then("2")
                        .when(dog.dogActivity.activityLevel.eq(ActivityLevel.NORMAL)).then("3")
                        .when(dog.dogActivity.activityLevel.eq(ActivityLevel.MUCH)).then("4")
                        .otherwise("5"))
                .from(dog)
                .where(dog.dogSize.eq(dogSize))
                .fetch();

        return results;
    }

    @Override
    public double findAvgTotalWalkingTimeByCity(String city) {
        Double result = queryFactory
                .select(dog.dogActivity.walkingTimePerOneTime.multiply(dog.dogActivity.walkingCountPerWeek).avg())
                .from(dog)
                .join(dog.member, member)
                .where(member.address.city.eq(city))
                .fetchOne();

        return result;
    }

    @Override
    public double findAvgTotalWalkingTimeByAge(double age) {
        Double result = queryFactory
                .select(dog.dogActivity.walkingTimePerOneTime.multiply(dog.dogActivity.walkingCountPerWeek).avg())
                .from(dog)
                .where(dog.startAgeMonth.divide(12).floor().eq((long) age))
                .fetchOne();

        return result;
    }

    @Override
    public double findAvgTotalWalkingTimeByDogSize(DogSize dogSize) {
        Double result = queryFactory
                .select(dog.dogActivity.walkingTimePerOneTime.multiply(dog.dogActivity.walkingCountPerWeek).avg())
                .from(dog)
                .where(dog.dogSize.eq(dogSize))
                .fetchOne();
        return result;
    }

    @Override
    public List<Long> findRanksById(Long id) {
        List<Long> results = queryFactory
                .select(dog.id)
                .from(dog)
                .orderBy((dog.dogActivity.walkingCountPerWeek.multiply(dog.dogActivity.walkingTimePerOneTime)).desc())
                .fetch();

        return results;
    }

    @Override
    public List<String> findSnackGroupByDogSize(DogSize dogSize) {
        List<String> results = queryFactory
                .select(new CaseBuilder()
                        .when(dog.snackCountLevel.eq(SnackCountLevel.LITTLE)).then("1")
                        .when(dog.snackCountLevel.eq(SnackCountLevel.NORMAL)).then("2")
                        .otherwise("3")
                )
                .from(dog)
                .where(dog.dogSize.eq(dogSize))
                .fetch();
        return results;
    }

    @Override
    public List<String> findDogNamesByMemberId(Long memberId) {
        NumberExpression<Integer> dogRank = new CaseBuilder()
                .when(dog.representative.isTrue()).then(1)
                .otherwise(2);
        List<Tuple> tuples = queryFactory
                .select(dog.name, dogRank)
                .from(dog)
                .where(dog.member.id.eq(memberId))
                .orderBy(dogRank.asc())
                .fetch();
        List<String> results = new ArrayList<>();
        for (Tuple tuple : tuples) {
            results.add(tuple.get(dog.name));
        }
        return results;
    }

    @Override
    public void updateAllDogRepresentativeFalse(Member member) {
        queryFactory
                .update(dog)
                .set(dog.representative, false)
                .where(dog.member.eq(member))
                .execute();
    }

    @Override
    public QueryDogDto findDogDtoByDog(Long id) {
        QueryDogDto.DogDto dogDto = queryFactory
                .select(Projections.constructor(QueryDogDto.DogDto.class,
                        dog.id,
                        dog.name,
                        dog.gender,
                        dog.birth,
                        dog.oldDog,
                        dog.dogType,
                        dog.dogSize,
                        dog.weight,
                        dog.neutralization,
                        dog.dogActivity.activityLevel,
                        dog.dogActivity.walkingCountPerWeek,
                        dog.dogActivity.walkingTimePerOneTime,
                        dog.dogStatus,
                        dog.snackCountLevel,
                        dog.inedibleFood,
                        dog.inedibleFoodEtc,
                        dog.recommendRecipe.id,
                        dog.caution
                ))
                .from(dog)
                .where(dog.id.eq(id))
                .fetchOne();

        List<RecipeSurveyResponseDto> recipeDtoList = recipeService.getRecipesForSurvey();

        List<String> ingredients = recipeService.getIngredients();

        QueryDogDto queryDogDto = QueryDogDto.builder()
                .dogDto(dogDto)
                .recipeDtoList(recipeDtoList)
                .ingredients(ingredients)
                .build();

        return queryDogDto;
    }

    @Override
    public List<QueryDogsDto> findDogsDtoByMember(Member member) {
        List<QueryDogsDto> result = queryFactory
                .select(Projections.constructor(QueryDogsDto.class,
                        dog.id,
                        dogPicture.filename,
                        dog.name,
                        dog.birth,
                        dog.gender,
                        dog.representative,
                        subscribe.status
                ))
                .from(dog)
                .join(dog.subscribe, subscribe)
                .leftJoin(dogPicture).on(dogPicture.dog.eq(dog))
                .where(validDogsByMember(member).and(dog.representative.eq(true)))
                .fetch();

        List<QueryDogsDto> addResult = queryFactory
                .select(Projections.constructor(QueryDogsDto.class,
                        dog.id,
                        dogPicture.filename,
                        dog.name,
                        dog.birth,
                        dog.gender,
                        dog.representative,
                        subscribe.status
                ))
                .from(dog)
                .join(dog.subscribe, subscribe)
                .leftJoin(dogPicture).on(dogPicture.dog.eq(dog))
                .where(validDogsByMember(member).and(dog.representative.eq(false)))
                .orderBy(dog.createdDate.asc())
                .fetch();
        result.addAll(addResult);

        for (QueryDogsDto dto : result) {
            if (dto.getPictureUrl() != null) {
                dto.changeUrl(dto.getPictureUrl());
            }
        }

        return result;
    }

    private BooleanExpression validDogsByMember(Member member) {
        return dog.member.eq(member).and(dog.isDeleted.eq(false));
    }


}
