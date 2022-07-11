package com.bi.barfdog.repository.card;

import com.bi.barfdog.api.cardDto.QuerySubscribeCardsDto;
import com.bi.barfdog.domain.member.Member;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class CardRepositoryImpl implements CardRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public QuerySubscribeCardsDto findSubscribeCards(Member member) {
//        queryFactory
        return null;
    }




}
