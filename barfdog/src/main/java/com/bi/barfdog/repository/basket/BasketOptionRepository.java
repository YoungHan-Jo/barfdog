package com.bi.barfdog.repository.basket;

import com.bi.barfdog.domain.basket.Basket;
import com.bi.barfdog.domain.basket.BasketOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BasketOptionRepository extends JpaRepository<BasketOption, Long>,BasketOptionRepositoryCustom {
    List<BasketOption> findAllByBasket(Basket basket);
}
