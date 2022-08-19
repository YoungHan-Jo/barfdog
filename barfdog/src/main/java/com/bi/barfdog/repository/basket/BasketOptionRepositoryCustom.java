package com.bi.barfdog.repository.basket;

import com.bi.barfdog.domain.basket.Basket;

import java.util.List;

public interface BasketOptionRepositoryCustom {
    void deleteAllByBasketIdList(List<Long> deleteBasketIdList);

    void deleteByBasketList(List<Basket> deleteBasketList);
}
