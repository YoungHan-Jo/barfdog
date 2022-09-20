package com.bi.barfdog.repository.basket;

import com.bi.barfdog.domain.basket.Basket;
import com.bi.barfdog.domain.basket.BasketOption;
import com.bi.barfdog.domain.basket.QBasketOption;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

    @Override
    public void deleteByBasketList(List<Basket> deleteBasketList) {
        queryFactory
                .delete(basketOption)
                .where(basketOption.basket.in(deleteBasketList))
                .execute();
    }

    @Override
    public Optional<BasketOption> findByOptionIdAndBasket(Long optionId, Basket basket) {
        BasketOption findBasketOption = queryFactory
                .selectFrom(QBasketOption.basketOption)
                .where(QBasketOption.basketOption.basket.eq(basket)
                        .and(QBasketOption.basketOption.itemOption.id.eq(optionId)))
                .fetchOne();

        return Optional.ofNullable(findBasketOption);
    }
}
