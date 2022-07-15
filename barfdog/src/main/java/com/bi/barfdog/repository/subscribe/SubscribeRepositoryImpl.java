package com.bi.barfdog.repository.subscribe;

import com.bi.barfdog.api.memberDto.MemberSubscribeAdminDto;
import com.bi.barfdog.api.memberDto.QuerySubscribeAdminDto;
import com.bi.barfdog.api.orderDto.OrderSheetSubscribeResponseDto;
import com.bi.barfdog.api.subscribeDto.QuerySubscribesDto;
import com.bi.barfdog.domain.dog.QDogPicture;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.recipe.QRecipe;
import com.bi.barfdog.domain.subscribe.Subscribe;
import com.bi.barfdog.domain.subscribeRecipe.QSubscribeRecipe;
import com.bi.barfdog.repository.subscribeRecipe.SubscribeRecipeRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static com.bi.barfdog.domain.dog.QDog.dog;
import static com.bi.barfdog.domain.dog.QDogPicture.*;
import static com.bi.barfdog.domain.member.QMember.member;
import static com.bi.barfdog.domain.recipe.QRecipe.*;
import static com.bi.barfdog.domain.subscribe.QSubscribe.subscribe;
import static com.bi.barfdog.domain.subscribeRecipe.QSubscribeRecipe.*;
import static com.bi.barfdog.domain.surveyReport.QSurveyReport.surveyReport;

@RequiredArgsConstructor
@Repository
public class SubscribeRepositoryImpl implements SubscribeRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    private final SubscribeRecipeRepository subscribeRecipeRepository;


    @Override
    public Page<MemberSubscribeAdminDto> findSubscribeAdminDtoByMemberId(Long id, Pageable pageable) {
        List<MemberSubscribeAdminDto> result = new ArrayList<>();

        List<QuerySubscribeAdminDto> querySubscribeAdminDtos = queryFactory
                .select(Projections.constructor(QuerySubscribeAdminDto.class,
                        subscribe.id,
                        dog.name,
                        subscribe.createdDate,
                        subscribe.subscribeCount,
                        subscribe.plan,
                        surveyReport.foodAnalysis.oneMealRecommendGram,
                        subscribe.nextPaymentPrice,
                        subscribe.nextDeliveryDate,
                        dog.inedibleFood,
                        dog.inedibleFoodEtc,
                        dog.caution
                ))
                .from(subscribe)
                .join(subscribe.dog, dog)
                .join(dog.member, member)
                .join(dog.surveyReport, surveyReport)
                .where(member.id.eq(id))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(subscribe.nextDeliveryDate.desc())
                .fetch();

        for (QuerySubscribeAdminDto querySubscribeAdminDto : querySubscribeAdminDtos) {

            List<String> recipeNames = subscribeRecipeRepository.findRecipeNamesBySubscribeId(querySubscribeAdminDto.getId());

            MemberSubscribeAdminDto memberSubscribeAdminDto = MemberSubscribeAdminDto.builder()
                    .querySubscribeAdminDto(querySubscribeAdminDto)
                    .recipeNames(recipeNames)
                    .build();

            result.add(memberSubscribeAdminDto);
        }

        Long totalCount = queryFactory
                .select(subscribe.count())
                .from(subscribe)
                .join(subscribe.dog, dog)
                .join(dog.member, member)
                .join(dog.surveyReport, surveyReport)
                .where(member.id.eq(id))
                .fetchOne();

        return new PageImpl<>(result,pageable,totalCount);
    }

    @Override
    public List<Subscribe> findWriteableByMember(Member member) {
        return queryFactory
                .select(subscribe)
                .from(subscribe)
                .where(subscribe.dog.member.eq(member).and(subscribe.writeableReview.isTrue()))
                .fetch()
                ;
    }

    @Override
    public OrderSheetSubscribeResponseDto.SubscribeDto findOrderSheetSubscribeDtoById(Long subscribeId) {
        return queryFactory
                .select(Projections.constructor(OrderSheetSubscribeResponseDto.SubscribeDto.class,
                        subscribe.id,
                        subscribe.plan,
                        subscribe.nextPaymentPrice
                ))
                .from(subscribe)
                .where(subscribe.id.eq(subscribeId))
                .fetchOne()
                ;
    }

    @Override
    public List<String> findRecipeNamesById(Long subscribeId) {
        return queryFactory
                .select(recipe.name)
                .from(subscribeRecipe)
                .join(subscribeRecipe.recipe, recipe)
                .where(subscribeRecipe.subscribe.id.eq(subscribeId))
                .fetch()
                ;

    }

    @Override
    public List<Subscribe> findAllByMember(Member user) {
        return queryFactory
                .select(subscribe)
                .from(subscribe)
                .join(subscribe.dog, dog)
                .join(dog.member, member)
                .where(member.eq(user))
                .orderBy(subscribe.nextDeliveryDate.asc())
                .fetch();
    }

    @Override
    public Page<QuerySubscribesDto> findSubscribesDto(Member member, Pageable pageable) {

        List<QuerySubscribesDto> result = new ArrayList<>();

        List<QuerySubscribesDto.SubscribeDto> subscribeDtoList = queryFactory
                .select(Projections.constructor(QuerySubscribesDto.SubscribeDto.class,
                        subscribe.id,
                        dogPicture.filename,
                        subscribe.isSkippable,
                        dog.name,
                        subscribe.plan,
                        subscribe.nextPaymentDate,
                        subscribe.nextPaymentPrice
                ))
                .from(subscribe)
                .join(subscribe.dog, dog)
                .leftJoin(dogPicture).on(dogPicture.dog.eq(dog))
                .where(dog.member.eq(member))
                .fetch();
        for (QuerySubscribesDto.SubscribeDto subscribeDto : subscribeDtoList) {
            subscribeDto.changeUrl(subscribeDto.getPictureUrl());

            String recipeNames = getRecipeNames(subscribeDto);

            QuerySubscribesDto dto = QuerySubscribesDto.builder()
                    .subscribeDto(subscribeDto)
                    .recipeNames(recipeNames)
                    .build();
            result.add(dto);
        }

        Long totalCount = queryFactory
                .select(subscribe.count())
                .from(subscribe)
                .join(subscribe.dog, dog)
                .where(dog.member.eq(member))
                .fetchOne();


        return new PageImpl<>(result, pageable, totalCount);
    }

    private String getRecipeNames(QuerySubscribesDto.SubscribeDto subscribeDto) {
        List<String> nameList = queryFactory
                .select(recipe.name)
                .from(subscribeRecipe)
                .join(subscribeRecipe.subscribe, subscribe)
                .join(subscribeRecipe.recipe, recipe)
                .where(subscribe.id.eq(subscribeDto.getSubscribeId()))
                .fetch();
        String recipeNames = nameList.get(0);
        if (nameList.size() > 1) {
            recipeNames += "," + nameList.get(1);
        }
        return recipeNames;
    }
}
