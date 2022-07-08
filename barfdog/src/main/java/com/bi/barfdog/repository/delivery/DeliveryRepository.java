package com.bi.barfdog.repository.delivery;

import com.bi.barfdog.domain.delivery.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Delivery,Long>,DeliveryRepositoryCustom {
}
