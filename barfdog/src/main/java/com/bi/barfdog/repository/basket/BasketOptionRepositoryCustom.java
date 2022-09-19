package com.bi.barfdog.repository.basket;

import com.bi.barfdog.domain.basket.Basket;
import com.bi.barfdog.domain.basket.BasketOption;

import java.util.List;
import java.util.Optional;

public interface BasketOptionRepositoryCustom {
    void deleteAllByBasketIdList(List<Long> deleteBasketIdList);

    void deleteByBasketList(List<Basket> deleteBasketList);

    Optional<BasketOption> findByOptionIdAndBasket(Long optionId, Basket basket);
}
