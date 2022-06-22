package com.bi.barfdog.repository.basket;

import java.util.List;

public interface BasketOptionRepositoryCustom {
    void deleteAllByBasketIdList(List<Long> deleteBasketIdList);
}
