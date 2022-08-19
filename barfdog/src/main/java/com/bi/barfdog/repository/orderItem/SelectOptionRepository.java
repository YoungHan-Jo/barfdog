package com.bi.barfdog.repository.orderItem;

import com.bi.barfdog.domain.basket.Basket;
import com.bi.barfdog.domain.orderItem.OrderItem;
import com.bi.barfdog.domain.orderItem.SelectOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SelectOptionRepository extends JpaRepository<SelectOption, Long> {
    List<SelectOption> findAllByOrderItem(OrderItem orderItem1);

}
