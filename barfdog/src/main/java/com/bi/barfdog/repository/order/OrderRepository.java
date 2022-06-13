package com.bi.barfdog.repository.order;

import com.bi.barfdog.domain.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
