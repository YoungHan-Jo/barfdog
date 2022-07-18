package com.bi.barfdog.repository.card;

import com.bi.barfdog.api.cardDto.QuerySubscribeCardsDto;
import com.bi.barfdog.domain.member.Member;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static com.bi.barfdog.domain.dog.QDog.*;
import static com.bi.barfdog.domain.member.QCard.*;
import static com.bi.barfdog.domain.recipe.QRecipe.*;
import static com.bi.barfdog.domain.subscribe.QSubscribe.*;
import static com.bi.barfdog.domain.subscribeRecipe.QSubscribeRecipe.*;

@RequiredArgsConstructor
@Repository
public class CardRepositoryImpl implements CardRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<QuerySubscribeCardsDto> findSubscribeCards(Member member) {

        List<QuerySubscribeCardsDto.SubscribeCardDto> subscribeCardDtoList = queryFactory
                .select(Projections.constructor(QuerySubscribeCardsDto.SubscribeCardDto.class,
                        subscribe.id,
                        card.id,
                        card.cardName,
                        card.cardNumber,
                        dog.name,
                        subscribe.plan,
                        subscribe.nextPaymentDate,
                        subscribe.nextPaymentPrice
                ))
                .from(subscribe)
                .join(subscribe.card, card)
                .join(subscribe.dog, dog)
                .where(card.member.eq(member))
                .fetch();

        List<QuerySubscribeCardsDto> result = new ArrayList<>();

        for (QuerySubscribeCardsDto.SubscribeCardDto subscribeCardDto : subscribeCardDtoList) {

            List<String> recipeNameList = queryFactory
                    .select(recipe.name)
                    .from(subscribeRecipe)
                    .join(subscribeRecipe.recipe, recipe)
                    .join(subscribeRecipe.subscribe, subscribe)
                    .where(subscribe.id.eq(subscribeCardDto.getSubscribeId()))
                    .fetch();

            QuerySubscribeCardsDto querySubscribeCardsDto = QuerySubscribeCardsDto.builder()
                    .subscribeCardDto(subscribeCardDto)
                    .recipeNameList(recipeNameList)
                    .build();

            result.add(querySubscribeCardsDto);
        }

        return result;
    }




}
