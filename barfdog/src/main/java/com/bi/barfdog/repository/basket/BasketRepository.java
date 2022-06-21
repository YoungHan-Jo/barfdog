package com.bi.barfdog.repository.basket;

import com.bi.barfdog.domain.basket.Basket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BasketRepository extends JpaRepository<Basket, Long>,BasketRepositoryCustom {
}
