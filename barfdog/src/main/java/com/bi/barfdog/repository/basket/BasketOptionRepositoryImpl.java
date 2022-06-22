package com.bi.barfdog.repository.basket;

import com.bi.barfdog.domain.basket.QBasketOption;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.bi.barfdog.domain.basket.QBasketOption.*;

@RequiredArgsConstructor
@Repository
public class BasketOptionRepositoryImpl implements BasketOptionRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public void deleteAllByBasketIdList(List<Long> deleteBasketIdList) {
        queryFactory
                .delete(basketOption)
                .where(basketOption.basket.id.in(deleteBasketIdList))
                .execute();
    }
}
