package com.bi.barfdog.repository.order;

import com.bi.barfdog.api.orderDto.OrderAdminCond;
import com.bi.barfdog.api.orderDto.QueryAdminOrdersDto;
import com.bi.barfdog.domain.delivery.Delivery;
import com.bi.barfdog.domain.order.Order;
import com.bi.barfdog.domain.order.SubscribeOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>,OrderRepositoryCustom {


    Optional<SubscribeOrder> findByMerchantUid(String nextOrderMerchant_uid);

    List<Order> findByDelivery(Delivery delivery);
}
