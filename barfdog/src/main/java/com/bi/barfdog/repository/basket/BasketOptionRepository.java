package com.bi.barfdog.repository.basket;

import com.bi.barfdog.domain.basket.Basket;
import com.bi.barfdog.domain.basket.BasketOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BasketOptionRepository extends JpaRepository<BasketOption, Long> {
    List<BasketOption> findAllByBasket(Basket basket);
}
